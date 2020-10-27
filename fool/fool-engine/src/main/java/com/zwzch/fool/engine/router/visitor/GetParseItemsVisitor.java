
package com.zwzch.fool.engine.router.visitor;

import com.alibaba.cobar.parser.ast.expression.Expression;
import com.alibaba.cobar.parser.ast.expression.primary.Identifier;
import com.alibaba.cobar.parser.ast.expression.primary.ParamMarker;
import com.alibaba.cobar.parser.ast.expression.primary.RowExpression;
import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLCreateTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLDropTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLTruncateStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DescTableStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.*;
import com.alibaba.cobar.parser.util.Pair;
import com.alibaba.cobar.parser.visitor.EmptySQLASTVisitor;
import com.zwzch.fool.engine.model.SqlObject;
import com.zwzch.fool.engine.router.model.ParseItem;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetParseItemsVisitor extends EmptySQLASTVisitor {
    private List<ParseItem> parseItemList = new ArrayList<ParseItem>();
	private SqlObject sqlObject;

	public GetParseItemsVisitor(SqlObject sqlObject) {
		this.sqlObject = sqlObject;
	}


	public List<ParseItem> getParseItemList() { return this.parseItemList; }


	@Override
	public void visit(DMLSelectStatement node) {
		noSplit(node);
	}


	@Override
    public void visit(DescTableStatement node) {
		noSplit(node);
    }


	@Override
    public void visit(DMLDeleteStatement node) {
		noSplit(node);
    }

	@Override
	public void visit(DDLCreateTableStatement node) {
		noSplit(node);
	}

	@Override
	public void visit(DDLDropTableStatement node) {
		noSplit(node);
	}

	@Override
	public void visit(DDLTruncateStatement node) {
		noSplit(node);
	}

	@Override
    public void visit(DMLUpdateStatement node) {
		noSplit(node);
    }

	private void noSplit(SQLStatement node) {
		GetParamIndexsVisitor gpiv = new GetParamIndexsVisitor();
		node.accept(gpiv);

		ParseItem parseItem = new ParseItem(sqlObject);
		parseItem.setParameterIndexs(gpiv.getParamIndexs());
		parseItem.sqlObject = sqlObject;
		parseItemList.add(parseItem);
	}


    @Override
    public void visit(DMLInsertStatement node) {
		splitSqlObject(node.getRowList());

        List<Pair<Identifier, Expression>> dup = node.getDuplicateUpdate();
        if (dup != null && !dup.isEmpty()) {
			GetParamIndexsVisitor gpiv = new GetParamIndexsVisitor();
            for (Pair<Identifier, Expression> p : dup) {
				p.getValue().accept(gpiv);
            }

			for(ParseItem parseItem : parseItemList) {
				parseItem.setUpdateParamIndexs(gpiv.getParamIndexs());
			}
        }
    }

    @Override
    public void visit(DMLReplaceStatement node) {
		splitSqlObject(node.getRowList());
    }

	/* 拆分批量处的sql */
	public void splitSqlObject(List<RowExpression> rowExpressionList) {
		if (null == rowExpressionList)
			return;
		
		for (int i=0; i<rowExpressionList.size(); i++) {
			RowExpression rowExpression = rowExpressionList.get(i);
			GetParamIndexsVisitor gpiv = new GetParamIndexsVisitor();
			rowExpression.accept(gpiv);

			ParseItem parseItem = new ParseItem(sqlObject);
			//parseItem.setSqlObject(sqlObject);
			parseItem.setInSqlIndex(i);
			parseItem.setParameterIndexs(gpiv.getParamIndexs());
			parseItemList.add(parseItem);
		}
	}
}
