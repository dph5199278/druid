/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_24 extends OracleTest {
    public void test_0() throws Exception {
        String sql = //
                "create table aliyun_cdm.test_905_table \n" +
                        "(col1 BIGINT,col2 STRING,col3 BOOLEAN,col4 DOUBLE,col5 DATETIME) row format delimited fields terminated by \"\\\\001\"\n"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE TABLE aliyun_cdm.test_905_table (\n" +
                    "\tcol1 BIGINT,\n" +
                    "\tcol2 STRING,\n" +
                    "\tcol3 BOOLEAN,\n" +
                    "\tcol4 DOUBLE,\n" +
                    "\tcol5 DATETIME\n" +
                    ")\n" +
                    "ROW FORMAT DELIMITED\n" +
                    "\tFIELDS TERMINATED BY '\\\\001'", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("aliyun_cdm.test_905_table"));

    }

    public void test_1_error() throws Exception {
        String sql = //
                "create table aliyun_cdm.test_905_table \n" +
                        "(col1 BIGINT,col2 STRING,col3 BOOLEAN,col4 DOUBLE,col5 DATETIME) row format delimited field terminated by \"\\\\001\"\n"; //

        Exception error = null;
        try {
            SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        } catch (ParserException ex) {
            error = ex;
        }
        assertNotNull(error);
        assertEquals("syntax error, expect FIELDS, pos 131, line 2, column 87, token IDENTIFIER field", error.getMessage());
    }
}
