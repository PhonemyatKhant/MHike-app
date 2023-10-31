package com.example.mhike;

public class ObservationDataModel {
    private int observationId;
    private int hikeId;
    private String observationText;
    private String observationTime;
    private String additionalComment;

    public int getObservationId() {
        return observationId;
    }

    public void setObservationId(int observationId) {
        this.observationId = observationId;
    }

    public int getHikeId() {
        return hikeId;
    }

    public void setHikeId(int hikeId) {
        this.hikeId = hikeId;
    }

    public String getObservationText() {
        return observationText;
    }

    public void setObservationText(String observationText) {
        this.observationText = observationText;
    }

    public String getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(String observationTime) {
        this.observationTime = observationTime;
    }

    public String getAdditionalComment() {
        return additionalComment;
    }

    public void setAdditionalComment(String additionalComment) {
        this.additionalComment = additionalComment;
    }
    public ObservationDataModel() {
    }

    public ObservationDataModel(int hikeId, String observationText, String observationTime, String additionalComment) {
        this.hikeId = hikeId;
        this.observationText = observationText;
        this.observationTime = observationTime;
        this.additionalComment = additionalComment;
    }

    public ObservationDataModel(int observationId, int hikeId, String observationText, String observationTime, String additionalComment) {
        this.observationId = observationId;
        this.hikeId = hikeId;
        this.observationText = observationText;
        this.observationTime = observationTime;
        this.additionalComment = additionalComment;
    }


}
