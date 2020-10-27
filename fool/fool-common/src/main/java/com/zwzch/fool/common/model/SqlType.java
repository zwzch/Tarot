package com.zwzch.fool.common.model;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.alibaba.cobar.parser.ast.stmt.dal.DALShowStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLCreateTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLDropTableStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DDLTruncateStatement;
import com.alibaba.cobar.parser.ast.stmt.ddl.DescTableStatement;
import com.alibaba.cobar.parser.ast.stmt.dml.*;

public enum SqlType {
	INSERT(0, true, false,"INSERT"), SELECT(1, false, false,"SELECT"), UPDATE(2, true, false,"UPDATE"), DELETE(3, true, false,"DELETE"), REPLACE(4, true, false,"REPLACE"),
	SHOW(5, false, false,"SHOW"), DESC(6, false, false,"DESC"),OTHER(7, false, false,"OTHER"),
	CREATE_TABLE(8, true, true,"CREATE_TABLE"), DROP_TABLE(9, true, true,"DROP_TABLE"), TRUNCATE_TABLE(10, true, true,"TRUNCATE_TABLE"),
	ALTER_TABLE(11, true, true,"ALTER_TABLE"),EXPLAIN(12, false, false,"EXPLAIN"),GET_SEQUENCEID(13, false, false,"GET_SEQUENCEID"),
	GET_SLICEID(14, false, false,"GET_SLICEID"),GET_TOPO(15, false, false,"GET_TOPO"),LAST_RULE_INFO(16, false, false,"LAST_RULE_INFO"),
	SELECT_SLEEP(17, false, false,"SELECT_SLEEP"),SHOW_CREATE_TABLE(18, false, false,"SHOW_CREATE_TABLE"),SHOW_TABLES(19, false, false,"SHOW_TABLES"),
	LAST_INSERTID(20, false, false,"LAST_INSERTID"),CONFIG_VERSION(21, false, false,"CONFIG_VERSION"),QUICK_FUSE_TABLE_MSG(22,false,false,"QUICK_FUSE_TABLE_MSG"),QUICK_FUSE_DB_MSG(23,false,false,"QUICK_FUSE_DB_MSG");

	private int i;
	private boolean isUpdate;
	private boolean isDDL;
	private String sqlTypeStr;

	SqlType(int i, boolean isUpdate, boolean isDDL, String sqlTypeStr) {
		this.i = i;
		this.isUpdate = isUpdate;
		this.isDDL = isDDL;
		this.sqlTypeStr = sqlTypeStr;
	}

	public boolean isUpdate() {
		return isUpdate;
	}
	public boolean isDDL() { return isDDL; }

	public static SqlType valueOf(int i) {
		if (i < 0 || i >= values().length) {
			throw new IndexOutOfBoundsException("SqlType valueOf - Invalid type index - index:" + i);
		}
		return values()[i];
	}

	public static SqlType valueOfSQLStatement(SQLStatement ss) {
		if (ss instanceof DMLInsertStatement) {
			return INSERT;
		} else if (ss instanceof DMLSelectStatement) {
			return SELECT;
		} else if (ss instanceof DMLUpdateStatement) {
			return UPDATE;
		} else if (ss instanceof DMLDeleteStatement) {
			return DELETE;
		} else if (ss instanceof DMLReplaceStatement) {
			return REPLACE;
		} else if( ss instanceof DALShowStatement) {
			return SHOW;
		} else if( ss instanceof DescTableStatement ) {
			return DESC;
		} else if( ss instanceof DDLCreateTableStatement) {
			return CREATE_TABLE;
		} else if( ss instanceof DDLDropTableStatement) {
			return DROP_TABLE;
		} else if( ss instanceof DDLTruncateStatement) {
			return TRUNCATE_TABLE;
		} else {
			//throw new NotSupportSQLException("only support select,insert,delete,update,replace statements");
			return OTHER;
		}
	}

	public static SqlType valueOfStr(String str) {
		if (str == null) {
			throw new IndexOutOfBoundsException("SqlType valueOfStr - type str is null");
		}

		if (str.equalsIgnoreCase("insert")) {
			return INSERT;
		}

		if (str.equalsIgnoreCase("select")) {
			return SELECT;
		}

		if (str.equalsIgnoreCase("update")) {
			return UPDATE;
		}

		if (str.equalsIgnoreCase("delete")) {
			return DELETE;
		}

		if (str.equalsIgnoreCase("replace")) {
			return REPLACE;
		}

		if (str.equalsIgnoreCase("show")) {
			return SHOW;
		}

		if (str.equalsIgnoreCase("desc")) {
			return DESC;
		}

		if (str.equalsIgnoreCase("create table")) {
			return CREATE_TABLE;
		}

		if (str.equalsIgnoreCase("drop table")) {
			return DROP_TABLE;
		}

		if (str.equalsIgnoreCase("truncate table")) {
			return TRUNCATE_TABLE;
		}

		if (str.equalsIgnoreCase("other")) {
			return OTHER;
		}

		throw new IndexOutOfBoundsException("SqlType valueOfStr - invalid type str - str:" + str);
	}

	public int Value() {
		return i;
	}

	public String toString() {
		return this.sqlTypeStr;
	}
}
