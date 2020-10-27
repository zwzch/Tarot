package com.zwzch.fool.engine.router.visitor;

import com.alibaba.cobar.parser.ast.expression.misc.InExpressionList;
import com.alibaba.cobar.parser.ast.expression.primary.*;
import com.alibaba.cobar.parser.ast.expression.primary.literal.*;
import com.alibaba.cobar.parser.ast.fragment.Limit;
import com.alibaba.cobar.parser.visitor.MySQLOutputASTVisitor;

public class GetFormatSQLVisitor extends MySQLOutputASTVisitor {
    public GetFormatSQLVisitor(StringBuilder appendable) {
        super(appendable);
    }

    public void visit(InExpressionList node) {
        appendable.append("(...)");
	}

    @Override
    public void visit(LiteralBitField node) {
        appendable.append("?");
    }

    @Override
    public void visit(LiteralBoolean node) {
        appendable.append("?");
    }

    @Override
    public void visit(LiteralHexadecimal node) {
        appendable.append("?");
    }

    @Override
    public void visit(LiteralNull node) {
        appendable.append("?");
    }

    @Override
    public void visit(LiteralNumber node) {
        appendable.append("?");
    }

    @Override
    public void visit(LiteralString node) {
        appendable.append("?");
    }

    @Override
    public void visit(ParamMarker paramMarker) {
        appendable.append("?");
    }

    @Override
    public void visit(Limit node) {
        appendable.append("LIMIT ");
        Object offset = node.getOffset();
        if(offset != null) {
            if (offset instanceof ParamMarker) {
                ((ParamMarker) offset).accept(this);
            } else {
                appendable.append("?");
            }
            appendable.append(", ");
        }

        Object size = node.getSize();
        if (size instanceof ParamMarker) {
            ((ParamMarker) size).accept(this);
        } else {
            appendable.append("?");
        }
    }
}
