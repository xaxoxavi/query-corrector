package com.esliceu;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavi on 4/03/18.
 */
public class TableNamesAndAliasFinder extends TablesNamesFinder {

    private Map<String,String> alias = new HashMap<>();

    @Override
    public void visit(Table tableName) {
        super.visit(tableName);

        if (tableName.getAlias() != null){

            String aliasStr = tableName.getAlias().getName();

            if (tableName.getAlias().isUseAs()){
                aliasStr = aliasStr.replaceAll("(?i)as","").trim();
            }

            alias.put(aliasStr,tableName.getName());
        }

    }

    public Map<String,String> getAlias(){
        return alias;
    }
}
