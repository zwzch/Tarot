package com.zwzch.fool.engine.router.visitor;

import java.util.List;

import com.alibaba.cobar.parser.ast.expression.Expression;
import com.alibaba.cobar.parser.ast.expression.misc.QueryExpression;
import com.alibaba.cobar.parser.ast.expression.primary.Identifier;
import com.alibaba.cobar.parser.ast.expression.primary.RowExpression;
import com.alibaba.cobar.parser.ast.fragment.Limit;
import com.alibaba.cobar.parser.ast.fragment.OrderBy;
import com.alibaba.cobar.parser.ast.fragment.tableref.IndexHint;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableRefFactor;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReferences;
import com.alibaba.cobar.parser.ast.stmt.ddl.DescTableStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLDeleteStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLInsertStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLReplaceStatement;
import com.alibaba.cobar.parser.util.Pair;
import com.alibaba.cobar.parser.visitor.MySQLOutputASTVisitor;
import com.zwzch.fool.common.constant.CommonConst;


/**
 * 对于不需要分库分表rule计算的操作单表的sql,去掉表名前的dbname。
 */
public class DeleteDBNameOutputVisitor extends MySQLOutputASTVisitor{
	private String ldbName;
    private String dbName = null;
    private List<String> ltbs;
    private List<String> alias;

    public DeleteDBNameOutputVisitor(StringBuilder appendable, String ldbName, String dbName, List<String> ltbs, List<String> alias) {
        super(appendable);
        this.ldbName = ldbName;
        this.dbName = dbName;
        this.ltbs = ltbs;
        this.alias = alias;
    }

    /**
     * 映射表名
     * @param force 强制替换表名,即使别名和表名相同
     */
    private void mapTableName(Identifier id, boolean force) {
    	Identifier realTable = null;
        // 别名和表名相同的情况下,别名的优先级比表名高
        if (id.getParent() != null && id.getParent().getIdTextUnescape().equals(ldbName)) {
            // 删除逻辑库名
            realTable = new Identifier(null, id.getIdTextUnescape());
        } else {
            //不去掉库名
            realTable = id;
        }
        realTable.accept(this);
	}

    private void mapTableName(Identifier id) {
        this.mapTableName(id, true);
    }

    /**
     * 忽略库名,表名作映射
     */
    @Override
    public void visit(Identifier node) {
        Identifier parent = node.getParent();
        if (parent != null) {
            mapTableName(parent, false);
            appendable.append('.');
        }
        appendable.append(node.getIdText());
    }

    @Override
    public void visit(TableRefFactor node) {
    	mapTableName(node.getTable());
        String alias = node.getAlias();
        if (alias != null) {
            appendable.append(" AS ").append(alias);
        }
        List<IndexHint> list = node.getHintList();
        if (list != null && !list.isEmpty()) {
            appendable.append(' ');
            printList(list, " ");
        }
    }
    
    @Override
    public void visit(DescTableStatement node) {
        appendable.append("DESC ");
        mapTableName(node.getTable());
    }
    

    @Override
    public void visit(DMLInsertStatement node) {
        appendable.append("INSERT ");
        switch (node.getMode()) {
        case DELAY:
            appendable.append("DELAYED ");
            break;
        case HIGH:
            appendable.append("HIGH_PRIORITY ");
            break;
        case LOW:
            appendable.append("LOW_PRIORITY ");
            break;
        case UNDEF:
            break;
        default:
            throw new IllegalArgumentException("unknown mode for INSERT: " + node.getMode());
        }
        if (node.isIgnore())
            appendable.append("IGNORE ");
        appendable.append("INTO ");
        //node.getTable().accept(this);
        //去掉表名前的dbname
        mapTableName(node.getTable());
        appendable.append(' ');

        List<Identifier> cols = node.getColumnNameList();
        if (cols != null) {
            appendable.append('(');
            printList(cols);
            appendable.append(") ");
        }

        QueryExpression select = node.getSelect();
        if (select == null) {
            appendable.append("VALUES ");
            List<RowExpression> rows = node.getRowList();
            if (rows != null && !rows.isEmpty()) {
                boolean isFst = true;
                for (RowExpression row : rows) {
                    if (row == null)
                        continue;
                    if (isFst)
                        isFst = false;
                    else
                        appendable.append(", ");
                    appendable.append('(');
                    printList(row.getRowExprList());
                    appendable.append(')');
                }
            } else {
                throw new IllegalArgumentException("at least one row for INSERT");
            }
        } else {
            select.accept(this);
        }

        List<Pair<Identifier, Expression>> dup = node.getDuplicateUpdate();
        if (dup != null && !dup.isEmpty()) {
            appendable.append(" ON DUPLICATE KEY UPDATE ");
            boolean isFst = true;
            for (Pair<Identifier, Expression> p : dup) {
                if (isFst)
                    isFst = false;
                else
                    appendable.append(", ");
                p.getKey().accept(this);
                appendable.append(" = ");
                p.getValue().accept(this);
            }
        }
    }
    
    @Override
    public void visit(DMLDeleteStatement node) {
        appendable.append("DELETE ");
        if (node.isLowPriority())
            appendable.append("LOW_PRIORITY ");
        if (node.isQuick())
            appendable.append("QUICK ");
        if (node.isIgnore())
            appendable.append("IGNORE ");
        TableReferences tableRefs = node.getTableRefs();
        if (tableRefs == null) {
            appendable.append("FROM ");
            //node.getTableNames().get(0).accept(this);
            //去掉表名前的dbname
            Identifier table = node.getTableNames().get(0);
            mapTableName(table);
        } else {
            boolean isFirst = true;
            for (Identifier id : node.getTableNames()) {
                if (isFirst)
                    isFirst = false;
                else
                    appendable.append(", ");
                mapTableName(id, false);
            }
            appendable.append(" FROM ");
            node.getTableRefs().accept(this);
        }
        Expression where = node.getWhereCondition();
        if (where != null) {
            appendable.append(" WHERE ");
            where.accept(this);
        }
        OrderBy orderBy = node.getOrderBy();
        if (orderBy != null) {
            appendable.append(' ');
            orderBy.accept(this);
        }
        Limit limit = node.getLimit();
        if (limit != null) {
            appendable.append(' ');
            limit.accept(this);
        }
    }
    

    @Override
    public void visit(DMLReplaceStatement node) {
        appendable.append("REPLACE ");
        switch (node.getMode()) {
        case DELAY:
            appendable.append("DELAYED ");
            break;
        case LOW:
            appendable.append("LOW_PRIORITY ");
            break;
        case UNDEF:
            break;
        default:
            throw new IllegalArgumentException("unknown mode for INSERT: " + node.getMode());
        }
        appendable.append("INTO ");
        //node.getTable().accept(this);
        //去掉表名前的dbname
        mapTableName(node.getTable());
        appendable.append(' ');

        List<Identifier> cols = node.getColumnNameList();
        if (cols != null && !cols.isEmpty()) {
            appendable.append('(');
            printList(cols);
            appendable.append(") ");
        }

        QueryExpression select = node.getSelect();
        if (select == null) {
            appendable.append("VALUES ");
            List<RowExpression> rows = node.getRowList();
            if (rows != null && !rows.isEmpty()) {
                boolean isFst = true;
                for (RowExpression row : rows) {
                    if (row == null || row.getRowExprList().isEmpty())
                        continue;
                    if (isFst)
                        isFst = false;
                    else
                        appendable.append(", ");
                    appendable.append('(');
                    printList(row.getRowExprList());
                    appendable.append(')');
                }
            } else {
                throw new IllegalArgumentException("at least one row for REPLACE");
            }
        } else {
            select.accept(this);
        }
    }


}
