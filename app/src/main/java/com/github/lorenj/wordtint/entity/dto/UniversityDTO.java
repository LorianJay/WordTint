package com.github.lorenj.wordtint.entity.dto;


/**
 * @author sukidayo
 * @date 2023/7/23 15:25
 */
public class UniversityDTO  {

    private String schoolName;

    private Integer code;

    private String city;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
