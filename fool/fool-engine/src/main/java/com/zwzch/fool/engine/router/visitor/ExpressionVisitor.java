
package com.zwzch.fool.engine.router.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.cobar.parser.ast.expression.BinaryOperatorExpression;
import com.alibaba.cobar.parser.ast.expression.Expression;
import com.alibaba.cobar.parser.ast.expression.PolyadicOperatorExpression;
import com.alibaba.cobar.parser.ast.expression.arithmeic.ArithmeticAddExpression;
import com.alibaba.cobar.parser.ast.expression.arithmeic.ArithmeticDivideExpression;
import com.alibaba.cobar.parser.ast.expression.arithmeic.ArithmeticIntegerDivideExpression;
import com.alibaba.cobar.parser.ast.expression.arithmeic.ArithmeticModExpression;
import com.alibaba.cobar.parser.ast.expression.arithmeic.ArithmeticMultiplyExpression;
import com.alibaba.cobar.parser.ast.expression.arithmeic.ArithmeticSubtractExpression;
import com.alibaba.cobar.parser.ast.expression.bit.BitAndExpression;
import com.alibaba.cobar.parser.ast.expression.bit.BitOrExpression;
import com.alibaba.cobar.parser.ast.expression.bit.BitShiftExpression;
import com.alibaba.cobar.parser.ast.expression.bit.BitXORExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.BetweenAndExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionEqualsExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionGreaterThanExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionGreaterThanOrEqualsExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionIsExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionLessOrGreaterThanExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionLessThanExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionLessThanOrEqualsExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionNotEqualsExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionNullSafeEqualsExpression;
import com.alibaba.cobar.parser.ast.expression.comparison.InExpression;
import com.alibaba.cobar.parser.ast.expression.logical.LogicalAndExpression;
import com.alibaba.cobar.parser.ast.expression.logical.LogicalOrExpression;
import com.alibaba.cobar.parser.ast.expression.logical.LogicalXORExpression;
import com.alibaba.cobar.parser.ast.expression.misc.AssignmentExpression;
import com.alibaba.cobar.parser.ast.expression.misc.InExpressionList;
import com.alibaba.cobar.parser.ast.expression.misc.QueryExpression;
import com.alibaba.cobar.parser.ast.expression.primary.*;
import com.alibaba.cobar.parser.ast.expression.primary.literal.LiteralBitField;
import com.alibaba.cobar.parser.ast.expression.primary.literal.LiteralBoolean;
import com.alibaba.cobar.parser.ast.expression.primary.literal.LiteralHexadecimal;
import com.alibaba.cobar.parser.ast.expression.primary.literal.LiteralNull;
import com.alibaba.cobar.parser.ast.expression.primary.literal.LiteralNumber;
import com.alibaba.cobar.parser.ast.expression.primary.literal.LiteralString;
import com.alibaba.cobar.parser.ast.expression.string.LikeExpression;
import com.alibaba.cobar.parser.ast.fragment.tableref.Dual;
import com.alibaba.cobar.parser.ast.fragment.tableref.IndexHint;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLSelectStatement;
import com.alibaba.cobar.parser.visitor.EmptySQLASTVisitor;
import com.zwzch.fool.engine.exception.NotSupportException;
import com.zwzch.fool.engine.exception.NotSupportSQLException;
import com.zwzch.fool.engine.model.Parameter;
import com.zwzch.fool.engine.router.value.LobValue;
import com.zwzch.fool.engine.router.value.NullValue;
import com.zwzch.fool.rule.exception.RuleRuntimeException;
import com.zwzch.fool.rule.param.IRuleParam;
import com.zwzch.fool.rule.param.IRuleParam.*;
import com.zwzch.fool.rule.param.RuleParamAnd;
import com.zwzch.fool.rule.param.RuleParamOr;
import com.zwzch.fool.rule.param.RuleParamSimple;

public class ExpressionVisitor extends EmptySQLASTVisitor{
    private Map<Integer, Parameter> parameterMap;
    private Map<String, String> asTableMap;

    private IRuleParam.TYPE type = IRuleParam.TYPE.UNDEFINE;
    private Object value;
    private String valueForLike;

    public IRuleParam comparative = null;

    public ExpressionVisitor(Map<Integer, Parameter> parameterMap, Map<String, String> asTableMap) {
        this.parameterMap = parameterMap;
        if(asTableMap==null) {
            this.asTableMap = new HashMap<String, String>();
        } else {
            this.asTableMap = asTableMap;
        }
    }
    
    @Override
    public void visit(BetweenAndExpression node) {
        Expression first = node.getFirst();
        Expression second = node.getSecond();
        Expression third = node.getThird();

        ExpressionVisitor v = new ExpressionVisitor(parameterMap, asTableMap);
        first.accept(v);

        ExpressionVisitor lv = new ExpressionVisitor(parameterMap, asTableMap);
        second.accept(lv);

        ExpressionVisitor rv = new ExpressionVisitor(parameterMap, asTableMap);
        third.accept(rv);

        if(node.isNot()) {
            RuleParamSimple left = this.buildRuleParamSimple(v, IRuleParam.OPERATION.LT, lv);
            RuleParamSimple right = this.buildRuleParamSimple(v, IRuleParam.OPERATION.GT, rv);

            RuleParamOr or = new RuleParamOr();
            or.addComparative(left);
            or.addComparative(right);
            this.comparative = or;
        } else {
            RuleParamSimple left = this.buildRuleParamSimple(v, OPERATION.GT_EQ, lv);
            RuleParamSimple right = this.buildRuleParamSimple(v, OPERATION.LT_EQ, rv);

            RuleParamAnd and = new RuleParamAnd();
            and.addComparative(left);
            and.addComparative(right);
            this.comparative = and;
        }
    }
    
    @Override
    public void visit(ComparisionIsExpression node) {
        IRuleParam ruleParam;

    	ExpressionVisitor leftEvi = new ExpressionVisitor(parameterMap, asTableMap);
        node.getOperand().accept(leftEvi);

        if (node.getMode() == ComparisionIsExpression.IS_NULL) {
            RuleParamSimple simple = new RuleParamSimple();
        	simple.setOp(OPERATION.IS_NULL);
            simple.setLeft(leftEvi.value, leftEvi.type);
            simple.setRight(NullValue.getNullValue(), IRuleParam.TYPE.UNDEFINE);
            ruleParam = simple;

        } else if (node.getMode() == ComparisionIsExpression.IS_NOT_NULL) {
            RuleParamSimple simple = new RuleParamSimple();
            simple.setOp(OPERATION.IS_NOT_NULL);
            simple.setLeft(leftEvi.value, leftEvi.type);
            simple.setRight(NullValue.getNullValue(), IRuleParam.TYPE.UNDEFINE);
            ruleParam = simple;

        } else if (node.getMode() == ComparisionIsExpression.IS_FALSE) {
            RuleParamSimple simple = new RuleParamSimple();
            simple.setOp(OPERATION.IS);
            simple.setLeft(leftEvi.value, leftEvi.type);
            simple.setRight(new Boolean(false), IRuleParam.TYPE.BOOL);
            ruleParam = simple;

        } else if (node.getMode() == ComparisionIsExpression.IS_NOT_FALSE) {
            RuleParamSimple simple = new RuleParamSimple();
            simple.setOp(OPERATION.IS);
            simple.setLeft(leftEvi.value, leftEvi.type);
            simple.setRight(new Boolean(true), IRuleParam.TYPE.BOOL);
            ruleParam = simple;

        } else if (node.getMode() == ComparisionIsExpression.IS_TRUE) {
            RuleParamSimple simple = new RuleParamSimple();
        	simple.setOp(OPERATION.IS);
            simple.setLeft(leftEvi.value, leftEvi.type);
        	simple.setRight(new Boolean(true), IRuleParam.TYPE.BOOL);
            ruleParam = simple;

        } else if (node.getMode() == ComparisionIsExpression.IS_NOT_TRUE) {
            RuleParamSimple simple = new RuleParamSimple();
        	simple.setOp(OPERATION.IS);
            simple.setLeft(leftEvi.value, leftEvi.type);
        	simple.setRight(new Boolean(false), IRuleParam.TYPE.BOOL);
            ruleParam = simple;

        } else if (node.getMode() == ComparisionIsExpression.IS_UNKNOWN) {
            /* TODO not support IS_UNKNOWN */
//            RuleParamNotSupport notSupport = new RuleParamNotSupport();
//            notSupport.addColumn(leftEvi.value.toString());
//            ruleParam = notSupport;
            throw new NotSupportSQLException("not supportec this ComparisionIsExpression mode:" + node.getMode());
        } else if (node.getMode() == ComparisionIsExpression.IS_NOT_UNKNOWN) {
            /* TODO not support IS_NOT_UNKNOWN */
//            RuleParamNotSupport notSupport = new RuleParamNotSupport();
//            notSupport.addColumn(leftEvi.value.toString());
//            ruleParam = notSupport;
            throw new NotSupportSQLException("not supportec this ComparisionIsExpression mode:" + node.getMode());
        } else {
            throw new NotSupportSQLException("not supportec this ComparisionIsExpression mode:" + node.getMode());
        }

        this.comparative = ruleParam;
    }
    
    @Override
    public void visit(BinaryOperatorExpression node) {

        if (node instanceof ComparisionEqualsExpression) {
            this.handleRuleParamSimple(node, OPERATION.EQ);
        } else if (node instanceof ComparisionGreaterThanExpression) {
            this.handleRuleParamSimple(node, OPERATION.GT);
        } else if (node instanceof ComparisionGreaterThanOrEqualsExpression) {
            this.handleRuleParamSimple(node, OPERATION.GT_EQ);
        } else if (node instanceof ComparisionLessOrGreaterThanExpression) {
            this.handleRuleParamSimple(node, OPERATION.NOT_EQ);
        } else if (node instanceof ComparisionLessThanExpression) {
            this.handleRuleParamSimple(node, OPERATION.LT);
        } else if (node instanceof ComparisionLessThanOrEqualsExpression) {
            this.handleRuleParamSimple(node, OPERATION.LT_EQ);
        } else if (node instanceof ComparisionNotEqualsExpression) {
            this.handleRuleParamSimple(node, OPERATION.NOT_EQ);
        } else if (node instanceof ComparisionNullSafeEqualsExpression) {
            /* TODO not support '<=>' */
            this.handleNotSupportParam(node);
        } else if (node instanceof ArithmeticAddExpression) {
        	/* TODO not support ArithmeticAddExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof ArithmeticDivideExpression) {
        	/* TODO not support ArithmeticDivideExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof ArithmeticIntegerDivideExpression) {
        	/* TODO not support ArithmeticIntegerDivideExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof ArithmeticModExpression) {
        	/* TODO not support ArithmeticModExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof ArithmeticMultiplyExpression) {
        	/* TODO not support ArithmeticMultiplyExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof ArithmeticSubtractExpression) {
        	/* TODO not support ArithmeticSubtractExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof AssignmentExpression) {
            /* TODO not support ':='  */
            this.handleNotSupportParam(node);
        } else if (node instanceof BitAndExpression) {
        	/* TODO not support BitAndExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof BitOrExpression) {
        	/* TODO not support BitOrExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof BitShiftExpression) {
        	/* TODO not support BitShiftExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof BitXORExpression) {
        	/* TODO not support BitXORExpression */
            this.handleNotSupportParam(node);
        } else if (node instanceof InExpression) {
            handleInExpression((InExpression) node);
        } else if (node instanceof LogicalXORExpression) {
        	  /* TODO not support LogicalXORExpression */
        } else {
            throw new NotSupportSQLException("not supported this BinaryOperatorExpression type " + node.getOperator());
        }
    }

    /* TIPS use (OR,EQ) to replace IN */
    private void handleInExpression(InExpression exp) {
    	RuleParamSimple simple = new RuleParamSimple();

    	ExpressionVisitor leftEv = new ExpressionVisitor(parameterMap, asTableMap);
    	exp.getLeftOprand().accept(leftEv);

    	simple.setLeft(leftEv.value, leftEv.type);

    	Expression right = exp.getRightOprand();
    	if(right instanceof InExpressionList){
            List<Expression> elist = ((InExpressionList) right).getList();

            RuleParamOr or = new RuleParamOr();
            for(Expression e : elist) {
                ExpressionVisitor v = new ExpressionVisitor(parameterMap, asTableMap);
                e.accept(v);

                or.addComparative(buildRuleParamSimple(leftEv, OPERATION.EQ, v));
            }

            this.comparative = or;
    	}else if(right instanceof QueryExpression){
    		throw new NotSupportSQLException("in expression contain query statements ");
    	}
	}


    
    @Override
    public void visit(LogicalAndExpression node) {
        //visit((PolyadicOperatorExpression) node);
    	RuleParamAnd and = new RuleParamAnd();
    	for (int i = 0; i < node.getArity(); i++) {
    		ExpressionVisitor ev = new ExpressionVisitor(parameterMap, asTableMap);
    		node.getOperand(i).accept(ev);
    	    and.addComparative(ev.getComparative());
    	}

        this.comparative = and;
    }

    @Override
    public void visit(LogicalOrExpression node) {
    	RuleParamOr or = new RuleParamOr();
    	for (int i = 0; i < node.getArity(); i++) {
    		ExpressionVisitor ev = new ExpressionVisitor(parameterMap, asTableMap);
    		node.getOperand(i).accept(ev);
    	    or.addComparative(ev.getComparative());
    	}

        this.comparative = or;
    }


    @Override
    public void visit(LikeExpression node) {
    	ExpressionVisitor left = new ExpressionVisitor(parameterMap, asTableMap);
    	node.getFirst().accept(left);
    	
    	ExpressionVisitor right = new ExpressionVisitor(parameterMap, asTableMap);
    	node.getSecond().accept(right);

        if(right.type==IRuleParam.TYPE.STRING && right.valueForLike!=null) {
            right.value = right.valueForLike;
        }
    	this.comparative = buildRuleParamSimple(left, OPERATION.LIKE, right);
    }
//
//    /* TODO */
//	@Override
//    public void visit(RowExpression node) {
//		//RuleParamAnd args = new RuleParamAnd();
//		List<Object> args = new ArrayList<Object>();
//        for (int i = 0; i < node.getRowExprList().size(); i++) {
//        	ExpressionVisitor mv = new ExpressionVisitor();
//            node.getRowExprList().get(i).accept(mv);
//            Object obj = mv.value;
//            args.add(obj);
//        }
//
//        this.value =  args;
//    }

    @Override
    public void visit(Identifier node) {
        Identifier identifier = node;
        String name = identifier.getIdTextUpUnescape();

        identifier = identifier.getParent();
        while(identifier!=null) {
            String pName = identifier.getIdTextUpUnescape();
            if(asTableMap.containsKey(pName)) {
                pName = asTableMap.get(pName);
            }

            name = pName + "." + name;
            identifier = identifier.getParent();
        }

    	this.value = name;
        this.type = IRuleParam.TYPE.COLUMN;
    }

    @Override
    public void visit(LiteralBitField node) {
        if (node.getIntroducer() != null) {
            throw new NotSupportException("bit value not support introducer:" + node.getIntroducer());
        }
        this.value = new LobValue(node.getText(), "b");
        this.type = IRuleParam.TYPE.BIT;
    }

    @Override
    public void visit(LiteralBoolean node) {
        this.value= node.isTrue();
        this.type = IRuleParam.TYPE.BOOL;
    }

    @Override
    public void visit(LiteralHexadecimal node) {
        if (node.getIntroducer() != null) {
            throw new NotSupportException("hex value not support introducer:" + node.getIntroducer());
        }

        this.value= new LobValue(node.getText(), "x");
        this.type = IRuleParam.TYPE.INT;
    }

    @Override
    public void visit(LiteralNull node) {
        this.value = NullValue.getNullValue();
        this.type = IRuleParam.TYPE.NULL;
    }

    @Override
    public void visit(LiteralNumber node) {
        this.value = (Comparable) node.getNumber();
        this.type = IRuleParam.TYPE.INT;
    }

    @Override
    public void visit(LiteralString node) {
        if (node.getIntroducer() != null) {
            this.value = new LobValue(node.getString(), node.getIntroducer());
            this.type = IRuleParam.TYPE.LOB;
        } else {
            // 由于是绑定变量的形式，普通的操作符要用转义后的，但是like的不行，似乎like比较特殊....
            this.value = node.getUnescapedString();
            this.valueForLike = node.getString();
            this.type = IRuleParam.TYPE.STRING;
        }

    }

    @Override
    public void visit(ParamMarker paramMarker) {
        int index = paramMarker.getParamIndex();
        this.type = parameterMap.get(index).getType();
        this.value = parameterMap.get(index).getValue();
    }
    
    @Override
    public void visit(PolyadicOperatorExpression node) {
        /* Override super method */
    }
    
    @Override
    public void visit(CaseWhenOperatorExpression node) {
        throw new NotSupportSQLException("CaseWhenOperatorExpression");
    }

    @Override
    public void visit(ExistsPrimary node) {
        throw new NotSupportSQLException("ExistsPrimary");
    }

    @Override
    public void visit(MatchExpression node) {
        throw new NotSupportSQLException("MatchExpression");
    }

    @Override
    public void visit(IndexHint node) {
        throw new NotSupportSQLException("IndexHint");
    }

    @Override
    public void visit(Dual dual) {
        throw new NotSupportSQLException("Dual");
    }



	public IRuleParam getComparative() {
		return comparative;
	}

    public Object getValue() {
        return this.value;
    }

    public  IRuleParam.TYPE getType() {
        return this.type;
    }

    private void handleNotSupportParam(BinaryOperatorExpression node) {
//        GetIdentifierVisitor giv = new GetIdentifierVisitor();
//        node.accept(giv);
//
//        this.comparative = new RuleParamNotSupport(giv.getColumnSet());
    }


	private void handleRuleParamSimple(BinaryOperatorExpression node,OPERATION op) {
        if (node.getRightOprand() instanceof DMLSelectStatement) {
            throw new NotSupportSQLException("Not support subquery");
        }

        ExpressionVisitor leftEv = new ExpressionVisitor(parameterMap, asTableMap);
        node.getLeftOprand().accept(leftEv);
        this.comparative = checkNotSupportForBinaryOP(leftEv, node.getRightOprand());
        if(this.comparative != null) {
            return ;
        }

        ExpressionVisitor rightEv = new ExpressionVisitor(parameterMap, asTableMap);
        node.getRightOprand().accept(rightEv);
        this.comparative = checkNotSupportForBinaryOP(rightEv, node.getLeftOprand());
        if(this.comparative != null) {
            return;
        }

        this.comparative = buildRuleParamSimple(leftEv, op, rightEv);
    }

    /* 检查是否有不支持的操作, 如果存在不支持的操作,则将整个expression涉及到的column提出出来  */
    private IRuleParam checkNotSupportForBinaryOP(ExpressionVisitor ev, Expression expression) {
        IRuleParam param = ev.getComparative();
        if(param==null) {
            return null;
        }

//        if(param instanceof RuleParamNotSupport) {
//            GetIdentifierVisitor giv = new GetIdentifierVisitor();
//            expression.accept(giv);
//
//            ((RuleParamNotSupport) param).addColumns(giv.getColumnSet());
//            return param;
//        }

        throw new RuleRuntimeException("checkNotSupportForBinaryOP param is not null and supported - param:" + param.toString());
    }

    private RuleParamSimple buildRuleParamSimple(ExpressionVisitor lv, OPERATION op, ExpressionVisitor rv) {
        RuleParamSimple simple = new RuleParamSimple();

        simple.setLeft(lv.value, lv.type);
        simple.setRight(rv.value, rv.type);
        simple.setOp(op);

        return simple;
    }
}
