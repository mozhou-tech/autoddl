package tech.mozhou.autoddl4j.source.java;

import com.google.common.base.CaseFormat;
import tech.mozhou.autoddl4j.TableDef;
import tech.mozhou.autoddl4j.source.java.annotations.TableExtend;
import tech.mozhou.autoddl4j.target.definition.CharacterSet;
import tech.mozhou.autoddl4j.target.definition.Collation;
import tech.mozhou.autoddl4j.target.definition.type.DbType;
import tech.mozhou.autoddl4j.target.definition.TableEngine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author liuyuancheng
 */
@Data
@Slf4j
public class TableParser {

    private Class<?> type;

    private TableDef.Table table;

    private TableParser() {

    }

    /**
     * Type 解析为表
     *
     * @param type
     * @param table
     */
    public static void parse(Class<?> type, TableDef.Table table) {
        final TableParser tableParser = new TableParser();
        tableParser.setTable(table);
        tableParser.setType(type);
        tableParser.execute();
    }

    private void execute() {
        this.table.setTableName(parseTableName(this.type))
                .setTableNameOriginal(this.type.getSimpleName())
                .setFormerName(parseFormerName(this.type))
                .setEngine(parseTableEngine(this.type))
                .setCollation(parseCollation(this.type))
                .setCharacterSet(parseCharacterSet(this.type))
                .setDataBaseType(parseDataBaseType(this.type))
                .setComment(parseComment(this.type))
        ;
    }

    private String[] parseFormerName(Class<?> type) {
        if (Objects.nonNull(type.getAnnotation(TableExtend.class))) {
            return type.getAnnotation(TableExtend.class).formerName();
        }
        return new String[0];
    }

    private CharacterSet parseCharacterSet(Class<?> type) {
        if (Objects.nonNull(type.getAnnotation(TableExtend.class))) {
            return type.getAnnotation(TableExtend.class).charset();
        }
        return CharacterSet.MySQL_UTF8;
    }

    private DbType parseDataBaseType(Class<?> type) {
        if (Objects.nonNull(type.getAnnotation(TableExtend.class))) {
            return type.getAnnotation(TableExtend.class).dbType();
        }
        return DbType.MySQL;
    }

    private Collation parseCollation(Class<?> type) {
        if (Objects.nonNull(type.getAnnotation(TableExtend.class))) {
            return type.getAnnotation(TableExtend.class).collation();
        }
        return Collation.MySQL_COLLATION_UTF8;
    }

    private TableEngine parseTableEngine(Class<?> type) {
        if (Objects.nonNull(type.getAnnotation(TableExtend.class))) {
            return type.getAnnotation(TableExtend.class).engine();
        }
        return TableEngine.MySQL_InnoDB;
    }

    private String parseComment(Class<?> type) {
        if (Objects.nonNull(type.getAnnotation(TableExtend.class))) {
            return type.getAnnotation(TableExtend.class).comment();
        }
        return "";
    }

    private String parseTableName(Class<?> type) {
        return readTableName(type);
    }

    public static String readTableName(Class<?> type) {
        String tableName = "";
        String tablePrefix = "";
        if (StringUtils.isEmpty(tableName) && Objects.nonNull(type.getAnnotation(javax.persistence.Table.class))) {
            tableName = type.getAnnotation(javax.persistence.Table.class).name();
        }
        if (StringUtils.isEmpty(tableName) && Objects.nonNull(type.getAnnotation(jakarta.persistence.Table.class))) {
            tableName = type.getAnnotation(jakarta.persistence.Table.class).name();
        }
        if (Objects.nonNull(type.getAnnotation(TableExtend.class))) {
            tablePrefix = type.getAnnotation(TableExtend.class).prefix();
        }
        if (StringUtils.isEmpty(tableName)) {
            tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, type.getSimpleName());
        }
        if (StringUtils.isNotEmpty(tablePrefix)) {
            tableName = tablePrefix + "_" + StringUtils.stripStart(tableName, "_");
        }
        return tableName;
    }
}
