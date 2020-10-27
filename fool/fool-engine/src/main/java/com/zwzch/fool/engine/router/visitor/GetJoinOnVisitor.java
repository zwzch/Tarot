
package com.zwzch.fool.engine.router.visitor;

import com.alibaba.cobar.parser.ast.expression.Expression;
import com.alibaba.cobar.parser.ast.expression.comparison.ComparisionEqualsExpression;
import com.alibaba.cobar.parser.ast.expression.primary.Identifier;
import com.alibaba.cobar.parser.ast.fragment.tableref.InnerJoin;
import com.alibaba.cobar.parser.ast.fragment.tableref.NaturalJoin;
import com.alibaba.cobar.parser.ast.fragment.tableref.OuterJoin;
import com.alibaba.cobar.parser.ast.fragment.tableref.StraightJoin;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableRefFactor;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReference;
import com.alibaba.cobar.parser.visitor.EmptySQLASTVisitor;
import com.zwzch.fool.engine.exception.NotSupportSQLException;
import com.zwzch.fool.engine.router.model.JoinOn;


public class GetJoinOnVisitor extends EmptySQLASTVisitor{
	
	private JoinOn joinOn = null;
	
	public JoinOn getJoinOn() {
		return joinOn;
	}
	
    @Override
    public void visit(InnerJoin node) {
    	processJoin(node.getLeftTableRef(),node.getRightTableRef(),node.getOnCond());  	
    }

    @Override
    public void visit(NaturalJoin node) {
    	throw new NotSupportSQLException("not support NaturalJoin");
    }

    @Override
    public void visit(OuterJoin node) {
    	//processJoin(node.getLeftTableRef(),node.getRightTableRef(),node.getOnCond());
    	throw new NotSupportSQLException("not support OuterJoin");
    }

    @Override
    public void visit(StraightJoin node) {
    	//processJoin(node.getLeftTableRef(),node.getRightTableRef(),node.getOnCond());
    	throw new NotSupportSQLException("not support StraightJoin");
    }
    
    private void processJoin(TableReference leftTableRef,TableReference rightTableRef,Expression onCond){
        if(!(leftTableRef instanceof TableRefFactor)||!(rightTableRef instanceof TableRefFactor)){
			throw new NotSupportSQLException("only support two table join");
    	}
        if(!(onCond instanceof ComparisionEqualsExpression)){
        	throw new NotSupportSQLException("join on condition only support ComparisionEqualsExpression");
        }
        
        ComparisionEqualsExpression on = (ComparisionEqualsExpression)onCond;
        if( !(on.getLeftOprand() instanceof Identifier) || !(on.getRightOprand() instanceof Identifier)){
        	throw new NotSupportSQLException("join on condition only support columns");
        }       
        Identifier onLeft = (Identifier) on.getLeftOprand();
        Identifier onRight = (Identifier) on.getRightOprand();
        if(onLeft.getParent()==null||onRight.getParent()==null){
        	throw new NotSupportSQLException("join on condition column must has table name");
        }
        
        TableRefFactor leftTable = (TableRefFactor)leftTableRef;
        TableRefFactor rightTable = (TableRefFactor)rightTableRef;
        
        String leftColumnName = null;
        String rightColumnName = null;
        if(leftTable.getAlias()!=null){
        	if(onLeft.getParent().getIdText().equals(leftTable.getAlias())||
        			onLeft.getParent().getIdText().equals(leftTable.getTable().getIdText())){
        		leftColumnName = onLeft.getIdText();
        		rightColumnName = onRight.getIdText();
        	}else{
        		leftColumnName = onRight.getIdText();
        		rightColumnName = onLeft.getIdText();
        	}
        }else{
        	if(onLeft.getParent().getIdText().equals(leftTable.getTable().getIdText())){
        		leftColumnName = onLeft.getIdText();
        		rightColumnName = onRight.getIdText();
        	}else{
        		leftColumnName = onRight.getIdText();
        		rightColumnName = onLeft.getIdText();
        	}
        	
        }
        
        
        this.joinOn = new JoinOn(leftTable.getTable().getIdText(),leftColumnName,
        		rightTable.getTable().getIdText(),rightColumnName);
    }

    
    

}
