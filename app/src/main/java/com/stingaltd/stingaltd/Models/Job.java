package com.stingaltd.stingaltd.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Job {
    private static final long serialVersionUID = 110L;
    private int job_id;
    private String job_comment;
    private String complete_date;

    public Job(int id, String comment) {
        this.job_id = id;
        this.job_comment = comment;
        this.complete_date = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.US).format(new Date());
    }

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
