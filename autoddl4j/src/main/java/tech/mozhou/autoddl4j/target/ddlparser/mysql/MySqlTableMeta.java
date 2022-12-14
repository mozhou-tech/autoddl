package tech.mozhou.autoddl4j.target.ddlparser.mysql;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.mozhou.autoddl4j.exception.Autoddl4jParserException;
import tech.mozhou.autoddl4j.target.ddlparser.AbstractTableMeta;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by liuyuancheng on 2022/10/26  <br/>
 *
 * @author liuyuancheng
 */
@Slf4j
public class MySqlTableMeta extends AbstractTableMeta {

    public MySqlTableMeta(JdbcTemplate jdbcTemplate) {
        super.setJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Boolean isTableExists(String tableName) {
        final List<Map<String, Object>> tables = getJdbcTemplate()
                .queryForList("show tables like " + StringUtils.wrapIfMissing(tableName, "'"));
        boolean exists = !tables.isEmpty();
        log.debug("table {} {} exists.", tableName, exists ? "is" : "is not");
        return exists;
    }

    @Override
    public List<String> showTableNames(String databaseName) {
        if (StringUtils.isEmpty(databaseName)) {
            try {
                final DataSource dataSource = getJdbcTemplate().getDataSource();
                Objects.requireNonNull(dataSource);
                final String url = dataSource.getConnection().getMetaData().getURL();
                Pattern pattern = Pattern.compile("/([a-zA-Z0-9_-]*?)\\?");
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    databaseName = matcher.toMatchResult().group(1);
                } else {
                    throw new Autoddl4jParserException("error");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        final List<Map<String, Object>> resultInMaps = getJdbcTemplate().queryForList("show tables;");
        String finalDatabaseName = databaseName;
        return resultInMaps.stream().map(e -> e.get("Tables_in_" + finalDatabaseName).toString()).collect(Collectors.toList());
    }

    /**
     * show create table table_name
     *
     * @return
     */
    @Override
    public String showCreateTable(@NonNull String tableName) {
        String createTable = "";
        if (this.isTableExists(tableName)) {
            final Map<String, Object> stringObjectMap = getJdbcTemplate().queryForMap("show create table " + StringUtils.wrapIfMissing(tableName, '`'));
            createTable = stringObjectMap.get("Create Table").toString();
        }else{
            log.warn("Showing table {} failed, in case of dose not exists.", tableName);
        }
        return createTable;
    }


}
