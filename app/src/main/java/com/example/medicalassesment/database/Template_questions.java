package com.example.medicalassesment.database;

import com.example.medicalassesment.models.FeedBackModel;
import com.example.medicalassesment.models.PreliminaryInfoModel;
import com.example.medicalassesment.models.QuestionModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Template_questions {

    @Expose
    @SerializedName("TemplatePreliminaryinfo")
    private java.util.List<PreliminaryInfoModel> TemplatePreliminaryinfo;
    @Expose
    @SerializedName("tempalteFeedbacks")
    private java.util.List<FeedBackModel> tempalteFeedbacks;
    @Expose
    @SerializedName("templateQuestions")
    private java.util.List<QuestionModel> templateQuestions;
    private List<PreliminaryInfoModel> defultPreliminaryInfo;
    public List<PreliminaryInfoModel> getTemplatePreliminaryinfo() {
        return TemplatePreliminaryinfo;
    }

    public void setTemplatePreliminaryinfo(List<PreliminaryInfoModel> TemplatePreliminaryinfo) {
        this.TemplatePreliminaryinfo = TemplatePreliminaryinfo;
    }

    public List<FeedBackModel> getTempalteFeedbacks() {
        return tempalteFeedbacks;
    }

    public void setTempalteFeedbacks(List<FeedBackModel> tempalteFeedbacks) {
        this.tempalteFeedbacks = tempalteFeedbacks;
    }

    public List<QuestionModel> getTemplateQuestions() {
        return templateQuestions;
    }

    public void setTemplateQuestions(List<QuestionModel> templateQuestions) {
        this.templateQuestions = templateQuestions;
    }


    public List<PreliminaryInfoModel> getDefultPreliminaryInfo() {
        return defultPreliminaryInfo;
    }

    public void setDefultPreliminaryInfo(List<PreliminaryInfoModel> defultPreliminaryInfo) {
        this.defultPreliminaryInfo = defultPreliminaryInfo;
    }
}
