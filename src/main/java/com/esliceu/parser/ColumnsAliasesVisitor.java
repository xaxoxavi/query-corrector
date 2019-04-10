package com.esliceu.parser;

import net.sf.jsqlparser.statement.select.*;

import java.util.HashMap;
import java.util.Map;

public class ColumnsAliasesVisitor  implements SelectVisitor, SelectItemVisitor {


    private Map<String,String> aliases = new HashMap<>();



    @Override
    public void visit(PlainSelect plainSelect) {

        aliases.clear();
        for (SelectItem item : plainSelect.getSelectItems()) {
            item.accept(this);
        }
    }

    @Override
    public void visit(SetOperationList setOpList) {
        for (PlainSelect select : setOpList.getPlainSelects()) {
            select.accept(this);
        }
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {

    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {

            if (selectExpressionItem.getAlias() != null) {
                aliases.put(selectExpressionItem.getExpression().toString(),
                        selectExpressionItem.getAlias().getName());
            } else {
                aliases.put(selectExpressionItem.getExpression().toString(),
                        null);
            }
    }

    @Override
    public void visit(WithItem withItem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(AllColumns allColumns) {

    }

    public Map<String, String> getAliases() {
        return aliases;
    }
}
