package com.code4j.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.code4j.annotation.Column;
import com.code4j.annotation.IgnoreReflection;
import com.code4j.annotation.PropertyKeyIndexId;
import com.code4j.annotation.Table;
import com.code4j.enums.DataSourceTypeEnum;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * 数据库连接信息
 *
 * @author liu_wp
 * @date 2020/11/17
 * @see
 */
@Table(value = "jdbc_source_info", uniqueKey = {"connect_name","connect_host", "connect_port", "source_type"})
@PropertyKeyIndexId
public class JdbcSourceInfo extends BaseInfo {
    /**
     * 索引位置
     */
    @Deprecated
    private Integer index;


    /**
     * 连接名称
     */
    @Column("connect_name")
    private String connectName;
    /**
     * 连接地址（域名或IP）
     */
    @Column("connect_host")
    private String connectHost;
    /**
     * 连接端口
     */
    @Column("connect_port")
    private Integer connectPort;
    /**
     * 用户名
     */
    @Column("user_name")
    private String userName;
    /**
     * 用户密码
     */
    @Column("password")
    private String password;
    @Column("source_type")
    private String sourceType;
    private String dbName;

    private String creator = "code4j";

    @IgnoreReflection
    private DataSourceTypeEnum dataSourceTypeEnum;
    @IgnoreReflection
    private List<JdbcDbInfo> jdbcDbInfos;
    @IgnoreReflection
    @JSONField(deserialize = false, serialize = false)
    private DefaultMutableTreeNode currTreeNode;


    @Override
    public JdbcSourceInfo clone() {
        try {
            JdbcSourceInfo jdbcSourceInfo = new JdbcSourceInfo();
            jdbcSourceInfo.setConnectName(this.getConnectName());
            jdbcSourceInfo.setConnectHost(this.getConnectHost());
            jdbcSourceInfo.setConnectPort(this.getConnectPort());
            jdbcSourceInfo.setUserName(this.getUserName());
            jdbcSourceInfo.setPassword(this.getPassword());
            jdbcSourceInfo.setCreator(this.getCreator());
            jdbcSourceInfo.setSourceType(this.getSourceType());
            jdbcSourceInfo.setDataSourceTypeEnum(this.getDataSourceTypeEnum());
            jdbcSourceInfo.setCurrTreeNode(this.getCurrTreeNode());
            return jdbcSourceInfo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.connectName;
    }

    /**
     * @param obj
     * @return
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        JdbcSourceInfo jdbcSourceInfo = (JdbcSourceInfo) obj;
        if (jdbcSourceInfo.getConnectHost() != null && jdbcSourceInfo.getConnectHost().equals(this.getConnectHost())
                && jdbcSourceInfo.getConnectPort() != null && jdbcSourceInfo.getConnectPort().equals(this.getConnectPort()) && jdbcSourceInfo.getConnectName().equals(this.getConnectName())) {
            return true;
        }
        return false;
    }

    public String getConnectName() {
        return connectName;
    }

    public void setConnectName(final String connectName) {
        this.connectName = connectName;
    }

    public String getConnectHost() {
        return connectHost;
    }

    public void setConnectHost(final String connectHost) {
        this.connectHost = connectHost;
    }

    public Integer getConnectPort() {
        return connectPort;
    }

    public void setConnectPort(final Integer connectPort) {
        this.connectPort = connectPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public DataSourceTypeEnum getDataSourceTypeEnum() {
        if (this.sourceType != null) {
            return this.dataSourceTypeEnum = DataSourceTypeEnum.getDataSourceTypeEnum(this.sourceType);
        }
        return dataSourceTypeEnum;
    }

    public void setDataSourceTypeEnum(final DataSourceTypeEnum dataSourceTypeEnum) {
        if (null != dataSourceTypeEnum) {
            this.sourceType = dataSourceTypeEnum.typeName();
        }
        this.dataSourceTypeEnum = dataSourceTypeEnum;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public String getSourceType() {
        if (null != dataSourceTypeEnum) {
            return this.sourceType = dataSourceTypeEnum.typeName();
        }
        return sourceType;
    }

    public void setSourceType(final String sourceType) {
        if (this.sourceType != null) {
            this.dataSourceTypeEnum = DataSourceTypeEnum.getDataSourceTypeEnum(this.sourceType);
        }
        this.sourceType = sourceType;
    }

    public List<JdbcDbInfo> getJdbcDbInfos() {
        return jdbcDbInfos;
    }

    public void setJdbcDbInfos(final List<JdbcDbInfo> jdbcDbInfos) {
        this.jdbcDbInfos = jdbcDbInfos;
    }

    public DefaultMutableTreeNode getCurrTreeNode() {
        return currTreeNode;
    }

    public void setCurrTreeNode(final DefaultMutableTreeNode currTreeNode) {
        this.currTreeNode = currTreeNode;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(final Integer index) {
        this.index = index;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
}
