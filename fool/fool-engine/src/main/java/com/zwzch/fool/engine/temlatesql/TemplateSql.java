package com.zwzch.fool.engine.temlatesql;

import com.alibaba.cobar.parser.recognizer.mysql.MySQLToken;
import com.alibaba.cobar.parser.recognizer.mysql.lexer.MySQLLexer;
import com.zwzch.fool.common.exception.CommonExpection;

import java.sql.SQLSyntaxErrorException;

import static com.alibaba.cobar.parser.recognizer.mysql.MySQLToken.EOF;
import static com.alibaba.cobar.parser.recognizer.mysql.MySQLToken.PUNC_SEMICOLON;

public class TemplateSql {
    public static StringBuffer getSqlTemplate(String sql) throws SQLSyntaxErrorException {
        StringBuffer sqlTemplate = new StringBuffer();

        MySQLLexer lexer = new MySQLLexer(sql);

        MySQLToken token = lexer.token();
        if (token == null) {
            try {
                token = lexer.nextToken();
            } catch (SQLSyntaxErrorException e) {
                throw new CommonExpection("lexer parser error :" + e);

            }
        }
        if (token == EOF) {
            return null;
        }
        while (token != PUNC_SEMICOLON && token != null && token != EOF){
            switch (token){
                case QUESTION_MARK:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_NUM_PURE_DIGIT:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_NUM_MIX_DIGIT:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_HEX:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_BIT:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_CHARS:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_NCHARS:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_NULL:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_BOOL_TRUE:
                    sqlTemplate.append("?");
                    break;
                case LITERAL_BOOL_FALSE:
                    sqlTemplate.append("?");
                    break;
//                case IDENTIFIER:
//                    pasql = pasql + lexer.stringValue().toString();
//                    break;
                case PLACE_HOLDER:
                    sqlTemplate.append(lexer.stringValue().toString());
                    break;
                case USR_VAR:
                    sqlTemplate.append(lexer.stringValue().toString());
                    break;
                case SYS_VAR:
                    sqlTemplate.append(lexer.stringValue().toString());
                    break;
                case PUNC_LEFT_PAREN:
                    sqlTemplate.append("(");
                    break;
                case PUNC_RIGHT_PAREN:
                    sqlTemplate.append(")");
                    break;
                case PUNC_LEFT_BRACE:
                    sqlTemplate.append("{");
                    break;
                case PUNC_RIGHT_BRACE:
                    sqlTemplate.append("}");
                    break;
                case PUNC_LEFT_BRACKET:
                    sqlTemplate.append("[");
                    break;
                case PUNC_RIGHT_BRACKET:
                    sqlTemplate.append("]");
                    break;
                case PUNC_COMMA:
                    sqlTemplate.append(",");
                    break;
                case PUNC_DOT:
                    sqlTemplate.append(".");
                    break;
                case PUNC_COLON:
                    sqlTemplate.append("]");
                    break;
                case PUNC_C_STYLE_COMMENT_END:
                    sqlTemplate.append("]");
                    break;
                case OP_EQUALS:
                    sqlTemplate.append("=");
                    break;
                case OP_GREATER_THAN:
                    sqlTemplate.append(">");
                    break;
                case OP_LESS_THAN:
                    sqlTemplate.append("<");
                    break;
                case OP_EXCLAMATION:
                    sqlTemplate.append("!");
                    break;
                case OP_TILDE:
                    sqlTemplate.append("~");
                    break;
                case OP_PLUS:
                    sqlTemplate.append("+");
                    break;
                case OP_MINUS:
                    sqlTemplate.append("-");
                    break;
                case OP_ASTERISK:
                    sqlTemplate.append("*");
                    break;
                case OP_SLASH:
                    sqlTemplate.append("/");
                    break;
                case OP_AMPERSAND:
                    sqlTemplate.append("&");
                    break;
                case OP_VERTICAL_BAR:
                    sqlTemplate.append("|");
                    break;
                case OP_CARET:
                    sqlTemplate.append("^");
                    break;
                case OP_PERCENT:
                    sqlTemplate.append("%");
                    break;
                case OP_ASSIGN:
                    sqlTemplate.append(":=");
                    break;
                case OP_LESS_OR_EQUALS:
                    sqlTemplate.append("<=");
                    break;
                case OP_LESS_OR_GREATER:
                    sqlTemplate.append("<>");
                    break;
                case OP_GREATER_OR_EQUALS:
                    sqlTemplate.append(">=");
                    break;
                case OP_NOT_EQUALS:
                    sqlTemplate.append("!=");
                    break;
                case OP_LOGICAL_AND:
                    sqlTemplate.append("&&");
                    break;
                case OP_LOGICAL_OR:
                    sqlTemplate.append("||");
                    break;
                case OP_LEFT_SHIFT:
                    sqlTemplate.append("<<");
                    break;
                case OP_RIGHT_SHIFT:
                    sqlTemplate.append(">>");
                    break;
                case OP_NULL_SAFE_EQUALS:
                    sqlTemplate.append("<=>");
                    break;
                default:
                    sqlTemplate.append(lexer.stringValue().toString());
            }

            try {
                token = lexer.nextToken();
            } catch (SQLSyntaxErrorException e) {
                throw new CommonExpection("lexer parser error :" + e);
            }
            sqlTemplate.append(" ");
        }
        return sqlTemplate;
    }

    public static void main(String[] args) throws SQLSyntaxErrorException {
        String sql = "select * from item_info where item_id = ? and b = 0 and c = 'abc'";
        System.out.println(getSqlTemplate(sql));
    }
}
