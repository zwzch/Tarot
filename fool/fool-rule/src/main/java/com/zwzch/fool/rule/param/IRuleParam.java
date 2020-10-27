package com.zwzch.fool.rule.param;

import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.rule.function.IFunctionConfig;
import com.zwzch.fool.rule.value.CompositeValue;

import java.util.Map;


public interface IRuleParam {

    public CompositeValue calc(IFunctionConfig config) throws CommonExpection;

    public void changeColumn(Map<String, String> columnMap);

	/**
	 * <p>功能描述：字段的类型</p>
	 */
    public enum TYPE{
		INT(0), STRING(1), COLUMN(2), NULL(3), LOB(4), BOOL(5), BIT(6), LIST(7), TIME(8), FLOAT(9), STRING2(10),
		WDMEMBER(11), SHOPCART(12), NCENTER(13), ADDR(14), UNDEFINE(15);

        private int i;

        TYPE(int i) { this.i = i; }

	    public static TYPE valueOf(int i) {
	        if (i < 0 || i >= values().length) {
	            throw new IndexOutOfBoundsException("IRuleParam.TYPE valueOf - Invalid type index - index:" + i);
	        }
	        return values()[i];
	    }

        public static TYPE valueOfStr(String str) {
            if(str==null) {
                throw new IndexOutOfBoundsException("IRuleParam.TYPE valueOfStr - type str is null");
            }

            if(str.equalsIgnoreCase("int")) {
                return INT;
            }

            if(str.equalsIgnoreCase("string")) {
                return STRING;
            }

			if(str.equalsIgnoreCase("string2")) {
				return STRING2;
			}

			if(str.equalsIgnoreCase("column")) {
				return COLUMN;
			}

			if(str.equalsIgnoreCase("null")) {
				return NULL;
			}

			if(str.equalsIgnoreCase("lob")) {
				return LOB;
			}

			if(str.equalsIgnoreCase("bool")) {
				return BOOL;
			}

			if(str.equalsIgnoreCase("list")) {
				return LIST;
			}

			if(str.equalsIgnoreCase("time")) {
				return TIME;
			}

			if(str.equalsIgnoreCase("float")) {
				return FLOAT;
			}

			if(str.equalsIgnoreCase("wdmember")) {
				return WDMEMBER;
			}

			if(str.equalsIgnoreCase("shopcart")) {
				return SHOPCART;
			}

			if(str.equalsIgnoreCase("ncenter")) {
				return NCENTER;
			}

			if(str.equalsIgnoreCase("addr")) {
				return ADDR;
			}

			if(str.equalsIgnoreCase("undefine")) {
				return UNDEFINE;
			}

            throw new IndexOutOfBoundsException("IRuleParam.TYPE valueOfStr - invalid type str - str:" + str);
        }

        public int Value() {
            return i;
        }

		@Override
	    public String toString() {
	        switch (this) {
                case INT:
                    return "INT";
                case STRING:
                    return "STRING";
				case STRING2:
					return "STRING2";
				case COLUMN:
					return "COLUMN";
				case NULL:
					return "NULL";
				case LOB:
					return "LOB";
				case BOOL:
					return "BOOL";
				case BIT:
					return "BIT";
				case LIST:
					return "LIST";
				case TIME:
					return "TIME";
				case FLOAT:
					return "FLOAT";
				case WDMEMBER:
					return "WDMEMBER";
				case SHOPCART:
					return "SHOPCART";
				case NCENTER:
					return "NCENTER";
				case ADDR:
					return "ADDR";
				case UNDEFINE:
					return "UNDEFINE";
	            default:
	                return null;
	        }
	    }
    }


	/**
	 * <p>功能描述：计算操作类型</p>
	 */
    public enum OPERATION {

	        AND(0), OR(1), GT(2), LT(3), GT_EQ(4), LT_EQ(5), EQ(6), LIKE(7), IS_NULL(8), IS_NOT_NULL(9), NOT_EQ(10),
	        IN(11), IS(12), CONSTANT(13), NULL_SAFE_EQUAL(14), XOR(15);

	        private int i;

	        OPERATION(int i){
	            this.i = i;
	        }

	        public static OPERATION valueOf(int i) {
	            if (i < 0 || i >= values().length) {
	                throw new IndexOutOfBoundsException("IRuleParam.OPERATION valueOf - Invalid operation index - index:" + i);
	            }
	            return values()[i];
	        }

	        public String toString() {
	            return String.valueOf(i);
	        }

	        public String getOPERATIONString() {
	            switch (this) {
	                case AND:
	                    return "AND";
	                case OR:
	                    return "OR";
	                case GT:
	                    return ">";
	                case LT:
	                    return "<";
	                case IN:
	                    return "IN";
	                case GT_EQ:
	                    return ">=";
	                case LT_EQ:
	                    return "<=";
	                case EQ:
	                    return "=";
	                case LIKE:
	                    return "LIKE";
	                case IS_NULL:
	                    return "IS NULL";
	                case IS:
	                    return "IS";
	                case IS_NOT_NULL:
	                    return "IS NOT NULL";
	                case NOT_EQ:
	                    return "!=";
	                case CONSTANT:
	                    return "CONSTANT";
	                case NULL_SAFE_EQUAL:
	                    return "<=>";
	                case XOR:
	                    return "XOR";
	                default:
	                    return null;
	            }
	        }

	        public OPERATION getInverse() {
	            switch (this) {
	                case AND:
	                    return AND;
	                case OR:
	                    return OR;
	                case GT:
	                    return LT;
	                case LT:
	                    return GT;
	                case IN:
	                    return IN;
	                case GT_EQ:
	                    return LT_EQ;
	                case LT_EQ:
	                    return GT_EQ;
	                case EQ:
	                    return EQ;
	                case LIKE:
	                    return LIKE;
	                case IS_NULL:
	                    return IS_NULL;
	                case IS:
	                    return IS;
	                case IS_NOT_NULL:
	                    return IS_NOT_NULL;
	                case NOT_EQ:
	                    return NOT_EQ;
	                case CONSTANT:
	                    return CONSTANT;
	                case NULL_SAFE_EQUAL:
	                    return NULL_SAFE_EQUAL;
	                case XOR:
	                    return XOR;
	                default:
	                    return null;
	            }
	        }
	    }
}
