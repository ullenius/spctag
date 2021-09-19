package se.anosh.spctag;

import org.junit.Before;
import org.junit.Test;
import se.anosh.spctag.dao.SpcDao;
import se.anosh.spctag.dao.SpcFile;
import se.anosh.spctag.domain.Xid6;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Xid6Test {

    private Path spc;
    private SpcDao dao;
    private Xid6 uut;

    @Before
    public void setup() throws IOException {
        System.out.println("hello");
        spc = Paths.get("spc/xid6.spc");
        dao = new SpcFile(spc.toString());
        uut = dao.readXid6();
    }

    @Test
    public void songName() {
       assertNull(uut.getSong());
    }

    

}
