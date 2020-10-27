
package com.zwzch.fool.engine.router.visitor;

import com.alibaba.cobar.parser.ast.expression.primary.ParamMarker;
import com.alibaba.cobar.parser.visitor.EmptySQLASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetParamIndexsVisitor extends EmptySQLASTVisitor{
    private List<Integer> paramIndexs = new ArrayList<Integer>();
    public List<Integer> getParamIndexs() {
        Collections.sort(this.paramIndexs);
        return this.paramIndexs;
    }

    @Override
    public void visit(ParamMarker paramMarker) {
        paramIndexs.add(paramMarker.getParamIndex());
    }
}
