package com.zwzch.fool.engine.model;



import com.zwzch.fool.common.constant.CommonConst;
import com.zwzch.fool.common.utils.StringUtils;
import com.zwzch.fool.engine.utils.SqlTypeUtils;
import com.zwzch.fool.rule.param.IRuleParam;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Parameter {
    private ParameterMethod parameterMethod;
    
    /**
     * args[0]: parameterIndex args[1]: 参数值 args[2]: length
     * 适用于：setAsciiStream、setBinaryStream、setCharacterStream、setUnicodeStream
     * 。。。
     */
	private int index;
    private Object[]        args;
    
    public Parameter(){}
	public Parameter(ParameterMethod parameterMethod, int index, Object[] args) {
		super();
		this.parameterMethod = parameterMethod;
		this.index = index;
		this.args = args;
	}

	public ParameterMethod getParameterMethod() { return parameterMethod; }
	public int getIndex() { return index; }
	public Object[] getArgs() { return args; }

	/* TODO process other type */
	public IRuleParam.TYPE getType() {
		ParameterMethod usedParameterMethod = preProcessObject();

		switch (usedParameterMethod) {
			case setArray:
			case setAsciiStream:
			case setBigDecimal:
			case setBinaryStream:
			case setBlob1:
			case setBlob2:
			case setBlob3:
			case setBytes:
			case setCharacterStream:
			case setClob:
			case setNull2:
			case setObject1:
			case setObject2:
			case setObject3:
			case setRef:
			case setURL:
			case setUnicodeStream:
				return IRuleParam.TYPE.STRING;

			case setBoolean:
				return IRuleParam.TYPE.BOOL;

			case setDate1:
			case setDate2:
			case setTime1:
			case setTime2:
			case setTimestamp1:
			case setTimestamp2:
				return IRuleParam.TYPE.TIME;

			case setDouble:
			case setFloat:
				return IRuleParam.TYPE.FLOAT;

			case setByte:
			case setShort:
			case setInt:
			case setLong:
				return IRuleParam.TYPE.INT;

			case setNull1:
				return IRuleParam.TYPE.NULL;

			case setString:
				return IRuleParam.TYPE.STRING;
		}

		throw new IllegalArgumentException("Unhandled ParameterMethod:" + parameterMethod);
	}

	/* TODO process other type */
	public String getValue() {
		ParameterMethod usedParameterMethod = preProcessObject();

		switch (usedParameterMethod) {
			case setArray:
			case setAsciiStream:
			case setBinaryStream:
			case setBlob1:
			case setBlob2:
			case setBlob3:
			case setBytes:
			case setCharacterStream:
			case setClob:
			case setNull2:
			case setObject1:
			case setObject2:
			case setObject3:
			case setRef:
			case setURL:
			case setUnicodeStream:
				return "null";

			case setBigDecimal:
				BigDecimal bigDecimal = (BigDecimal)args[0];
				return bigDecimal.toString();

			case setBoolean:
				Boolean b = (Boolean) args[0];
				if (b) {
					return "TRUE";
				} else {
					return "FALSE";
				}

			case setDate1:
			case setDate2:
				if (args[0] != null) {
					Date date1 = (Date) args[0];
					return gmtToString(date1.getTime());
				} else {
					return "null";
				}

			case setDouble:
				Double d = (Double) args[0];
				return d.toString();

			case setFloat:
				Float f = (Float) args[0];
				return f.toString();

			case setInt:
				Integer i = (Integer) args[0];
				return i.toString();

			case setLong:
				Long l = (Long) args[0];
				return l.toString();

			case setNull1:
				return "NULL";

			case setByte:
				Byte aByte = (Byte)args[0];
				return aByte.toString();

			case setShort:
				Short s = (Short) args[0];
				return s.toString();

			case setString:
				String str = (String) args[0];
				return str;

			case setTime1:
			case setTime2:
				if (args[0] != null) {
					Time time1 = (Time) args[0];
					return gmtToString(time1.getTime());
				} else {
					return "null";
				}

			case setTimestamp1:
			case setTimestamp2:
				if (args[0] != null) {
					Timestamp timestamp1 = (Timestamp) args[0];
					return gmtToString(timestamp1.getTime());
				} else {
					return "null";
				}
		}

		throw new IllegalArgumentException("Unhandled ParameterMethod:" + parameterMethod);
	}

	@Override
	public String toString(){
		String str;
		if(parameterMethod!=null)
		    str = parameterMethod.toString();
		else
			str = "";

		for(Object o:args){
			if(o!=null)
			    str+=o.toString()+",";
		}
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Parameter)) {
			return false;
		}

		Parameter parameter = (Parameter)obj;
		if(this.parameterMethod != parameter.parameterMethod) {
			return false;
		}

		if(!StringUtils.isEquals(this.args, parameter.args)) {
			return false;
		}

		return true;
	}

	private static String gmtToString(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(CommonConst.DEFAULT_TIME_FORMAT);
		Date date = new Date(time);

		return sdf.format(date);
	}

	/**
	 * 使用parameter之前先做预处理
	 *  如将setObject转化成特定的类型
	 *
	 *
	 *
	 * @return 实际使用的parameterMethod
	 */
	private ParameterMethod preProcessObject() {
		switch (parameterMethod) {
			case setObject1:
				return SqlTypeUtils.convertSetObjectByType(args[0]);

			case setObject2:
			case setObject3:
				return parameterMethod;

			default:
				return parameterMethod;
		}

	}
}
