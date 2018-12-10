package com.stingaltd.stingaltd.Models;

public class Job {
    private static final long serialVersionUID = 110L;
    private int job_id;
    private String job_comment;
    private String complete_date;

    public int getJobId() {
        return job_id;
    }

    public String getJobComment() {
        return job_comment;
    }

    public String getCompleteDate() {
        return complete_date;
    }
}
