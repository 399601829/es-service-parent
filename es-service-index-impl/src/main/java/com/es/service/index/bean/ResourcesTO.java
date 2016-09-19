package com.es.service.index.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.es.service.common.util.CompletionSuggest;
import com.es.service.common.util.CompletionSuggest.SuggestBuilder;
import com.es.service.common.util.PinYinHelper;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 类/接口注释
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月22日
 * 
 */
public class ResourcesTO extends BaseTO {

    /**
     * 
     */
    private static final long serialVersionUID = 7675849584559838783L;

    /**
     * type
     */
    @JsonProperty("TYPE")
    private int type;

    /**
     * res_key(type:id)
     */
    @JsonProperty("RES_KEY")
    private String res_key;
    /**
     * 名称
     */
    @JsonProperty("NAME")
    private String name;

    /**
     * 英文
     */
    @JsonProperty("ENNAME")
    private String enName;

    /**
     * 拼音
     */
    @JsonProperty("NAME_PINYIN")
    private String name_pinyin;

    /**
     * 拼音首字母
     */
    @JsonProperty("NAME_PREFIX_PINYIN")
    private String name_prefix_pinyin;

    /**
     * 全部的拼音字段
     */
    @JsonProperty("NAME_PINYIN_QUERY")
    private String name_pinyin_query;

    /**
     * 别名
     */
    @JsonProperty("ALIAS")
    private String alias;

    /**
     * 标签
     */
    @JsonProperty("TAGS")
    private String tags;

    /**
     * 错别字
     */
    @JsonProperty("TYPOS")
    private String typos;

    /**
     * icon
     */
    @JsonProperty("ICON")
    private String icon;

    /**
     * 描述
     */
    @JsonProperty("DESCRIPTION")
    private String description;

    /**
     * 热度，热度越高，同等名词搜索下得分应该更高
     */
    @JsonProperty("HOT")
    private float hot;

    /**
     * 修改时间，时间越近，同等名词搜索下得分应该更高
     */
    @JsonProperty("MODIFIDATE")
    private Date modifiDate;

    /**
     * 创建时间
     */
    @JsonProperty("CREATEDATE")
    private Date createDate;

    /**
     * 状态，0为删除，1为正常
     */
    @JsonProperty("STATUS")
    private int status;

    /**
     * 建议器-自动补缺
     */
    @JsonProperty(CompletionSuggest.key)
    private SuggestBuilder suggest;

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the enName
     */
    public String getEnName() {
        return enName;
    }

    /**
     * @param enName the enName to set
     */
    public void setEnName(String enName) {
        if (StringUtils.isNotBlank(enName)) {
            this.enName = enName.toLowerCase();
        }
    }

    /**
     * @return the name_pinyin
     */
    public String getName_pinyin() {
        if (StringUtils.isBlank(name_pinyin)) {
            setName_pinyin(PinYinHelper.getInstance().getPinYin(name));
        }
        return name_pinyin;
    }

    /**
     * @param name_pinyin the name_pinyin to set
     */
    public void setName_pinyin(String name_pinyin) {
        this.name_pinyin = name_pinyin;
    }

    /**
     * @return the name_prefix_pinyin
     */
    public String getName_prefix_pinyin() {
        if (StringUtils.isBlank(name_prefix_pinyin)) {
            setName_prefix_pinyin(PinYinHelper.getInstance().getPinYinPrefix(name));
        }
        return name_prefix_pinyin;
    }

    /**
     * @param name_prefix_pinyin the name_prefix_pinyin to set
     */
    public void setName_prefix_pinyin(String name_prefix_pinyin) {
        this.name_prefix_pinyin = name_prefix_pinyin;
    }

    /**
     * @return the name_pinyin_query
     */
    public String getName_pinyin_query() {
        if (StringUtils.isBlank(name_pinyin_query)) {
            StringBuilder all = new StringBuilder();
            if (StringUtils.isNotBlank(name)) {
                all.append(PinYinHelper.getInstance().getAnalyzePinYin(name)).append(",");
                all.append(PinYinHelper.getInstance().getAnalyzePinYinPrefix(name)).append(",");
            }
            if (StringUtils.isNotBlank(alias)) {
                String[] str = alias.split(",");
                for (int i = 0; i < str.length; i++) {
                    all.append(PinYinHelper.getInstance().getAnalyzePinYin(str[i])).append(",");
                    all.append(PinYinHelper.getInstance().getAnalyzePinYinPrefix(str[i])).append(
                            ",");
                }
            }
            setName_pinyin_query(all.toString());
        }
        return name_pinyin_query;
    }

    /**
     * @param name_pinyin_query the name_pinyin_query to set
     */
    public void setName_pinyin_query(String name_pinyin_query) {
        this.name_pinyin_query = name_pinyin_query;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * @return the typos
     */
    public String getTypos() {
        return typos;
    }

    /**
     * @param typos the typos to set
     */
    public void setTypos(String typos) {
        this.typos = typos;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the hot
     */
    public float getHot() {
        return hot;
    }

    /**
     * @param hot the hot to set
     */
    public void setHot(float hot) {
        this.hot = hot;
    }

    /**
     * @return the modifiDate
     */
    public Date getModifiDate() {
        return modifiDate;
    }

    /**
     * @param modifiDate the modifiDate to set
     */
    public void setModifiDate(Date modifiDate) {
        this.modifiDate = modifiDate;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the suggest
     */
    public SuggestBuilder getSuggest() {
        if(suggest == null){
            setSuggest();
        }
        return suggest;
    }

    /**
     * @return the res_key
     */
    public String getRes_key() {
        return type + ":" + id;
    }

    /**
     * @param res_key the res_key to set
     */
    public void setRes_key(String res_key) {
        this.res_key = res_key;
    }

    /**
     * @param suggest the suggest to set
     */
    public void setSuggest() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("RES_KEY", getRes_key());
        payload.put("ID", getId());
        payload.put("NAME", getName());

        SuggestBuilder builder = new SuggestBuilder(name, payload);

        this.suggest = builder;
    }

}
