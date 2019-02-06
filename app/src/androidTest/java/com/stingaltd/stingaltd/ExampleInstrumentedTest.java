package com.stingaltd.stingaltd;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.JobItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest
{
    Context c;

    @Before
    public void setUp()
    {
        c = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void useAppContext() {
        assertEquals("com.stingaltd.stingaltd", c.getPackageName());
    }

    @Test
    public void CountEqual4Test()
    {
        List<JobItem> obj = (List<JobItem>) Common.readObjectFromFile(c, Common.getUserJobsFileName(c));
        assertEquals(obj.size(), 4);
    }

    @Test
    public void JobId2Test()
    {
        JobItem T = Common.FilterJobById(c,2);
        assertEquals(T.getTitle(), "Fix brocken cable");
    }

    @Test
    public void JobIndexTest()
    {
        int idx = Common.GetJobByIdx(c,2);
        assertEquals(3, idx);
    }

    @Test
    public void GetJobFileNameTest()
    {
        String name = Common.getUserJobsFileName(c);
        assertEquals("jobs_demo.json",name);
    }

    @Test
    public void TechNoteTest()
    {
        JobItem T = Common.FilterJobById(c,2);
        assertEquals(T.getTechnicianNote(), "hello world 123");
    }

    @Test
    public void GetJobTaskListTest()
    {
        //TaskExecutor T = new TaskExecutor(c);
        //assertEquals(3,T.GetTaskList().size());
    }
}
