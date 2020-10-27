
package com.zwzch.fool.engine.router.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import clojure.lang.Obj;
import com.alibaba.cobar.parser.ast.expression.Expression;
import com.alibaba.cobar.parser.ast.expression.misc.QueryExpression;
import com.alibaba.cobar.parser.ast.expression.primary.Identifier;
import com.alibaba.cobar.parser.ast.expression.primary.ParamMarker;
import com.alibaba.cobar.parser.ast.expression.primary.literal.LiteralNumber;
import com.alibaba.cobar.parser.ast.fragment.Limit;
import com.alibaba.cobar.parser.ast.fragment.OrderBy;
import com.alibaba.cobar.parser.ast.fragment.tableref.IndexHint;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableRefFactor;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReference;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReferences;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLCreateTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLDropTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLTruncateStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DescTableStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLDeleteStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLInsertStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLReplaceStatement;
import com.alibaba.cobar.parser.util.Pair;
import com.alibaba.cobar.parser.visitor.MySQLOutputASTVisitor;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.model.Parameter;


/**
 *  重写sql
 *  1. 替换表名&去掉dbname
 *  2. insert和replace中增加自增id
 */
public class OutputVisitor extends MySQLOutputASTVisitor{
    private String ldbName;
	private Map<String,String> logicTabToPhysicalTabMap = new HashMap<String,String>();
	
//	private SeqResult seqResult = null;
	private int inSqlIndex=0;
	private Map<Integer, Parameter> parameterMap;

    private String dbName = null;
    private List<String> alias;
    //private ParseItem parseItem;

	public OutputVisitor(StringBuilder appendable,Map<String,String> logicTabToPhysicalTabMap, Object seqResult,
			int inSqlIndex,Map<Integer, Parameter> parameterMap,String ldbName, String dbName,List<String> alias) {
		super(appendable);
		this.logicTabToPhysicalTabMap = logicTabToPhysicalTabMap;
//        this.seqResult = seqResult;
        this.inSqlIndex = inSqlIndex;
        this.parameterMap = parameterMap;
        this.ldbName = ldbName;
        this.dbName = dbName;
        this.alias = alias;
	}

    @Override
    public void visit(TableRefFactor node) {
        //表名替换
        convertToRealTableName(node.getTable());
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
        convertToRealTableName(node.getTable());
    }

    @Override
    public void visit(DDLCreateTableStatement node) {
        appendable.append("CREATE TABLE ");
        convertToRealTableName(node.getTable());
        appendable.append(node.getSubString());
    }

    @Override
    public void visit(DDLTruncateStatement node) {
        appendable.append("TRUNCATE TABLE ");
        convertToRealTableName(node.getTable());
    }

    @Override
    public void visit(DDLDropTableStatement node) {
        appendable.append("DROP ");
        if (node.isTemp()) {
            appendable.append("TEMPORARY ");
        }
        appendable.append("TABLE ");
        if (node.isIfExists()) {
            appendable.append("IF EXISTS ");
        }

        convertToRealTableName(node.getTableNames().get(0));

        switch (node.getMode()) {
            case CASCADE:
                appendable.append(" CASCADE");
                break;
            case RESTRICT:
                appendable.append(" RESTRICT");
                break;
            case UNDEF:
                break;
            default:
                throw new IllegalArgumentException("unsupported mode for DROP TABLE: " + node.getMode());
        }
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
        convertToRealTableName(node.getTable());
        appendable.append(' ');

        boolean haveSeq = false;
//        if(this.seqResult!=null) {
//            haveSeq = true;
//        }

        List<Identifier> cols = node.getColumnNameList();
//        if(haveSeq) {
//            cols.add(new Identifier(cols.get(0).getParent(), this.seqResult.getKey()));
//        }
        if (cols != null) {
            appendable.append('(');
            printList(cols);
            appendable.append(") ");
        }
        if(haveSeq) {
            cols.remove(cols.size()-1);
        }

        QueryExpression select = node.getSelect();
//        if (select != null) {
//            throw new NotSupportException("not support select in insert sql");
//        }

        appendable.append("VALUES ");

        List<Expression> values = node.getRowList().get(this.inSqlIndex).getRowExprList();
        if (haveSeq) {
            addSeqValue(values);
        }
        appendable.append('(');
        printList(values);
        appendable.append(')');
        if (haveSeq) {
            delSeqValue(values);
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
        convertToRealTableName(node.getTable());
        appendable.append(' ');


        boolean haveSeq = false;
//        if (this.seqResult != null) {
//            haveSeq = true;
//        }

        List<Identifier> cols = node.getColumnNameList();
//        if (haveSeq) {
//            cols.add(new Identifier(cols.get(0).getParent(), this.seqResult.getKey()));
//        }
        if (cols != null && !cols.isEmpty()) {
            appendable.append('(');
            printList(cols);
            appendable.append(") ");
        }
        if (haveSeq) {
            cols.remove(cols.size() - 1);
        }


        QueryExpression select = node.getSelect();
        if (select != null) {
            throw new CommonExpection("not support select in replace sql");
        }

        /* 将value写入 */
        appendable.append("VALUES ");

        List<Expression> values = node.getRowList().get(this.inSqlIndex).getRowExprList();
        if (haveSeq) {
            addSeqValue(values);
        }
        appendable.append('(');
        printList(values);
        appendable.append(')');
        if (haveSeq) {
            delSeqValue(values);
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
            //表名替换
            Identifier logicTable = node.getTableNames().get(0);
            convertToRealTableName(logicTable);
            //node.getTableNames().get(0).accept(this);
        } else {
            boolean isFirst = true;
            for (Identifier id : node.getTableNames()) {
                if (isFirst)
                    isFirst = false;
                else
                    appendable.append(", ");
                convertToRealTableName(id, false);
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
	
	/**
	 * 替换掉列名前的逻辑表名
	 */
    @Override
    public void visit(Identifier node) {
        Identifier parent = node.getParent();
        if (parent != null) {
            convertToRealTableName(parent, false);
            appendable.append('.');
        }
        appendable.append(node.getIdText());
    }

    private void addSeqValue(List<Expression> expressionList) {
        if(this.parameterMap.size()>0) {
            /* 加入到paramater中 */
            expressionList.add(new ParamMarker(-1));

        } else {
            /* 加入到sql中 */
//            expressionList.add(new LiteralNumber(this.seqResult.getValue()));
        }
    }

    private void delSeqValue(List<Expression> expressionList) {
        expressionList.remove(expressionList.size()-1);
    }

    /**
     * 表名替换&去掉dbname
     * @param force 强制替换表名,即使别名和表名相同
     */
    private void convertToRealTableName(Identifier identifier, boolean force) {
        String tableName = null;
        if (!this.alias.contains(identifier.getIdTextUnescape()) || force) {
            tableName = logicTabToPhysicalTabMap.get(identifier.getIdTextUnescape());
        }
        if (tableName != null) {//表名替换
//            if (dbName != null) {
//                tableName = ConstValue.DB_SHADOW_STR + dbName + ConstValue.MIDDLE_SHADOW_STR + tableName;
//            }
            Identifier realTable = null;
            if ((identifier.getParent() != null && identifier.getParent().getIdTextUnescape().equals(ldbName)) || dbName != null) {
                //去掉库名
                realTable = new Identifier(null, tableName);
            } else {
                //不去库名
                realTable = new Identifier(identifier.getParent(), tableName);
            }
            realTable.accept(this);
        } else {//不做表名替换
            if (dbName != null && !this.alias.contains(identifier.getIdTextUnescape())) {
//                identifier = new Identifier(null, ConstValue.DB_SHADOW_STR + dbName + ConstValue.MIDDLE_SHADOW_STR + identifier.getIdTextUnescape());
            } else if (identifier.getParent() != null && identifier.getParent().getIdTextUnescape().equals(ldbName)) {
                //去掉库名
                identifier = new Identifier(null, identifier.getIdTextUnescape());
            }

            identifier.accept(this);
        }
    }
    /**
     * 表名替换&去掉dbname
     */
    private void convertToRealTableName(Identifier identifier) {
        this.convertToRealTableName(identifier, true);
    }
}
