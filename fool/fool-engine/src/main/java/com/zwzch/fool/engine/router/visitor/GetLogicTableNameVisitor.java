package com.zwzch.fool.engine.router.visitor;

import com.alibaba.cobar.parser.ast.expression.primary.Identifier;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableRefFactor;
import com.alibaba.cobar.parser.ast.fragment.tableref.TableReferences;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLDeleteStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLInsertStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.DMLReplaceStatement;
import com.alibaba.cobar.parser.visitor.EmptySQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class GetLogicTableNameVisitor extends EmptySQLASTVisitor {
    private List<String> ltNames = new ArrayList<String>();
    private List<String> aliasNames = new ArrayList<String>();

    public List<String> getLtNames() {
        return ltNames;
    }

    public GetLogicTableNameVisitor() {
        super();
    }

    @Override
    public void visit(DMLDeleteStatement node) {
        TableReferences tableRefs = node.getTableRefs();
        if (null == tableRefs) {
            addTable(node.getTableNames().get(0));
        } else
            tableRefs.accept(this);
        super.visit(node);
    }

    @Override
    public void visit(DMLInsertStatement node) {
        this.addTable(node.getTable());
        super.visit(node);
    }

    @Override
    public void visit(DMLReplaceStatement node) {
        this.addTable(node.getTable());
        super.visit(node);
    }

    @Override
    public void visit(TableRefFactor node) {
        this.aliasNames.add(node.getAliasUnescape(false));
        this.ltNames.add(node.getTable().getIdTextUnescape());
    }

    private void addTable(List<Identifier> identifiers) {
        for (Identifier id : identifiers) {
            this.addTable(id);
        }
    }

    private void addTable(Identifier identifier) {
        this.ltNames.add(identifier.getIdTextUnescape());
    }

    public List<String> getAliasNames() {
        return aliasNames;
    }

}