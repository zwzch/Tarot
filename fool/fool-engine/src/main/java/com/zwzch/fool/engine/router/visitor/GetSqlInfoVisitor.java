package com.zwzch.fool.engine.router.visitor;

import java.util.*;

import com.alibaba.cobar.parser.ast.expression.primary.Identifier;
import com.alibaba.cobar.parser.ast.expression.primary.ParamMarker;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableRefFactor;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReference;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReferences;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLCreateTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLDropTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLTruncateStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DescTableStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.*;
import com.alibaba.cobar.parser.visitor.EmptySQLASTVisitor;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.engine.exception.NotSupportSQLException;
import com.zwzch.fool.engine.model.SqlObject;

/**
 * 基础sql检查 
 * 获取LogicTableName,ColumnSet
 * 判断是否到默认库执行
 *
 */
public class GetSqlInfoVisitor extends EmptySQLASTVisitor{
	private SqlObject sqlObject;

	public GetSqlInfoVisitor(SqlObject sqlObject) {
		super();
		this.sqlObject = sqlObject;
	}

	@Override
	public void visit(DMLDeleteStatement node) {
		//process(node.getTableNames());
		List<Identifier> tableNameList = node.getTableNames();
		if(tableNameList == null||tableNameList.size() == 0) {
		     throw new NotSupportSQLException("DeleteStatement without table name");
	    }
		process(tableNameList.get(0));
		super.visit(node);
	}

	@Override
	public void visit(DMLInsertStatement node) {
		visitInsertReplace(node);
		super.visit(node);
	}

	@Override
	public void visit(DMLReplaceStatement node) {
		visitInsertReplace(node);
		super.visit(node);
	}

	protected void visitInsertReplace(DMLInsertReplaceStatement node) {
		if (node.getColumnNameList() == null || node.getColumnNameList().size() == 0) {
			throw new NotSupportSQLException("replace/insert column is null");
		}

		if (node.getRowList().get(0).getRowExprList().size() != node.getColumnNameList().size()) {
			throw new NotSupportSQLException("replace/insert column.size!=value.size");
		}

		process(node.getTable());
		getColumnSet(node.getColumnNameList());
	}
	
	@Override
	public void visit(DMLSelectStatement node) {
		TableReferences tr = node.getTables();
		if(tr == null||tr.getTableReferenceList()==null || tr.getTableReferenceList().size() != 1){
//			sqlObject.getHintObject().setDefaultDB(true);  // TODO 多个表名
		}else{
			super.visit(node);
		}
	}
	

	@Override
	public void visit(DMLUpdateStatement node) {
		List<TableReference> tl = node.getTableRefs().getTableReferenceList();
		if(tl==null||tl.size()==0){
			throw new NotSupportSQLException("UpdateStatement without table name");
		}

		super.visit(node);
	}

	@Override
	public void visit(DDLCreateTableStatement node) {
		process(node.getTable());
	}

	@Override
    public void visit(DDLTruncateStatement node) { process(node.getTable()); }

	@Override
	public void visit(DDLDropTableStatement node) {
		process(node.getTableNames().get(0));
	}

	@Override
	public void visit(DescTableStatement node) { process(node.getTable()); }
	
	
	/*check marker is set*/
    @Override
    public void visit(ParamMarker paramMarker) {
        int index = paramMarker.getParamIndex();
        if(!sqlObject.getParameterMap().containsKey(index)) {
            String execpStr = "marker is not set, index:" + index;
            throw new CommonExpection(execpStr);
        }
    }

	private void getColumnSet(List<Identifier> columnList) {
		for(Identifier identifier : columnList) {
			sqlObject.getParseResult().getColumnSet().add(identifier.getIdTextUpUnescape());
		}
	}

    @Override
    public void visit(TableRefFactor node) {
        Identifier table = node.getTable();
        process(table);

        // 处理as的情况
		String tableName = node.getTable().getIdTextUpUnescape();
		String asName = node.getAliasUnescapeUppercase();
		sqlObject.getParseResult().getAsTableMap().put(asName,tableName);
	}

//	private void process(List<Identifier> identifiers) {
//    	if(identifiers==null) {
//    		return;
//		}
//
//		for(Identifier identifier : identifiers) {
//			process(identifier);
//		}
//	}

    private void process(Identifier table){
    	String ldbName = sqlObject.getDataSource().getLogicDBName();
    	if(table.getParent()!=null&&!table.getParent().getIdTextUnescape().equals(ldbName)){
//			sqlObject.getHintObject().setDefaultDB(true);
			return;
    	}
    	String tableName = table.getIdTextUnescape();
		sqlObject.getParseResult().getLogicTables().add(tableName);
    }

	public SqlObject getSqlObject() {
		return sqlObject;
	}

//	private void process(TableReferences tr) {
//		if (tr == null) {
//			return;
//		}
//
//		List<TableReference> tableReferences = tr.getTableReferenceList();
//		for (TableReference tableReference : tableReferences) {
//			tableReference.accept(this);
//		}
//	}
}
