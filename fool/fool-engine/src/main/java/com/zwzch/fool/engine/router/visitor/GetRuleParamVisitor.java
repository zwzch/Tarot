
package com.zwzch.fool.engine.router.visitor;

import com.alibaba.cobar.parser.ast.expression.Expression;
import com.alibaba.cobar.parser.ast.expression.misc.QueryExpression;
import com.alibaba.cobar.parser.ast.expression.primary.Identifier;
import com.alibaba.cobar.parser.ast.expression.primary.RowExpression;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReference;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReferences;
import com.alibaba.cobar.parser.ast.stmt.dml.*;
import com.alibaba.cobar.parser.visitor.EmptySQLASTVisitor;
import com.zwzch.fool.engine.exception.NotSupportSQLException;
import com.zwzch.fool.engine.model.Parameter;
import com.zwzch.fool.engine.router.model.ParseItem;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.param.RuleParamSimple;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class GetRuleParamVisitor extends EmptySQLASTVisitor{
	private ParseItem parseItem;

	public GetRuleParamVisitor(ParseItem parseItem) {
		this.parseItem = parseItem;
	}

	private IRuleParam ruleParams;

	@Override
	public void visit(DMLSelectStatement node) {
		/* TODO 多表join不支持 */
		TableReferences tr = node.getTables();
		if(tr.getTableReferenceList().size()>1){
			throw new NotSupportSQLException("Not support select with mutli tables");
		}

		Expression where = node.getWhere();
		if(where!=null){
			ExpressionVisitor ev = new ExpressionVisitor(parseItem.getSqlObject().getParameterMap(), parseItem.getSqlObject().getParseResult().getAsTableMap());
			where.accept(ev);
			this.ruleParams = ev.getComparative();
		}else{
			this.ruleParams = null;
		}

	}

	@Override
	public void visit(DMLUpdateStatement node) {
		List<TableReference> tl = node.getTableRefs().getTableReferenceList();
	    if(tl.size()>1){
	    	throw new NotSupportSQLException("Not support update with mutli tables");
	    }

	    Expression where = node.getWhere();
	    if(where != null){
			ExpressionVisitor ev = new ExpressionVisitor(parseItem.getSqlObject().getParameterMap(), parseItem.getSqlObject().getParseResult().getAsTableMap());
			where.accept(ev);
			this.ruleParams = ev.getComparative();
	    }else{
			this.ruleParams = null;
	    }
	}

	@Override
	public void visit(DMLDeleteStatement node) {
		List<Identifier> tableNameList = node.getTableNames();
		if(tableNameList.size()>1) {
			throw new NotSupportSQLException("Not support delete with mutli tables");
		}

		Expression where = node.getWhereCondition();
		if(where!=null){
			ExpressionVisitor ev = new ExpressionVisitor(parseItem.getSqlObject().getParameterMap(), parseItem.getSqlObject().getParseResult().getAsTableMap());
			where.accept(ev);
			this.ruleParams = ev.getComparative();
		}else{
			this.ruleParams = null;
		}

	}

//	@Override
//	public void visit(DMLInsertStatement node) {
//        // 暂时不支持子表的查询
//        QueryExpression subQuery = node.getSelect();
//        if (subQuery != null) {
//            throw new NotSupportSQLException("could not support insert into select");
//        }
//
//		ruleParams = getRuleParamForInsertReplace(node.getColumnNameList(),
//				node.getRowList(), parseItem.getInSqlIndex(),
//				parseItem.getSqlObject().getParameterMap(),
//				parseItem.getSeqResult());
//	}
//
//	@Override
//	public void visit(DMLReplaceStatement node) {
//        // 暂时不支持子表的查询
//        QueryExpression subQuery = node.getSelect();
//        if (subQuery != null) {
//            throw new NotSupportSQLException("could not support replace into select");
//        }
//
//		ruleParams = getRuleParamForInsertReplace(node.getColumnNameList(),
//				node.getRowList(), parseItem.getInSqlIndex(),
//				parseItem.getSqlObject().getParameterMap(),
//				parseItem.getSeqResult());
//	}


//	private IRuleParam getRuleParamForInsertReplace(List<Identifier> columnList,
//													List<RowExpression> rowExpressions, int index,
//													Map<Integer, Parameter> parameterMap,
//													SeqResult seqResult) {
//		List<Expression> expressionList = rowExpressions.get(index).getRowExprList();
//
//		if(columnList.size() == 1 && seqResult==null){
//			ExpressionVisitor ev = new ExpressionVisitor(parameterMap, parseItem.getSqlObject().getParseResult().getAsTableMap());
//			expressionList.get(0).accept(ev);
//
//			RuleParamSimple simple = new RuleParamSimple();
//			simple.setLeft(columnList.get(0).getIdTextUpUnescape(), IRuleParam.TYPE.COLUMN);;
//			simple.setOp(IRuleParam.OPERATION.EQ);
//			simple.setRight(ev.getValue(), ev.getType());
//			return simple;
//		} else {
//			RuleParamAnd and = new RuleParamAnd();
//			for(int i=0; i<columnList.size(); i++) {
//				ExpressionVisitor ev = new ExpressionVisitor(parameterMap, parseItem.getSqlObject().getParseResult().getAsTableMap());
//				expressionList.get(i).accept(ev);
//
//				RuleParamSimple simple = new RuleParamSimple();
//				simple.setLeft(columnList.get(i).getIdTextUpUnescape(), IRuleParam.TYPE.COLUMN);
//				simple.setOp(IRuleParam.OPERATION.EQ);
//				simple.setRight(ev.getValue(), ev.getType());
//				and.addComparative(simple);
//			}
//
//			if(seqResult!=null) {
//				RuleParamSimple simple = new RuleParamSimple();
//				simple.setLeft(seqResult.getKey(), IRuleParam.TYPE.COLUMN);
//				simple.setOp(IRuleParam.OPERATION.EQ);
//				simple.setRight(seqResult.getValue(), IRuleParam.TYPE.INT);
//				and.addComparative(simple);
//			}
//
//			return and;
//		}
//	}

	public IRuleParam getRuleParams() {
		return ruleParams;
	}

	public void setRuleParams(IRuleParam ruleParams) {
		this.ruleParams = ruleParams;
	}
	
	
	
}
