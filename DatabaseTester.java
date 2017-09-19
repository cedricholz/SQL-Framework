import db.Database;
import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseTester {

    @Test
    public void testLoad() {
        Database db = new Database();
        String result = db.transact("load t7");
        assertEquals(result, "");

        result = db.transact("load tables/DNE.tbl");
        assertTrue(!result.equals(""));
    }

    @Test
    public void testJoin() {
        Database db = new Database();
        db.transact("load t7");
        db.transact("load t8");
        String result = db.transact("select * from t7,t8");

        String expected = "z int,w int,x int,y int,b int\n4,1,7,7,7\n9,1,1,9,11";
        assertEquals(result, expected);
    }

    @Test
    public void testExample() {
        Database db = new Database();
        String actual, expected;

        actual = db.transact("load fans");
        expected = "";
        assertEquals(expected, actual);

        actual = db.transact("load teams");
        expected = "";
        assertEquals(expected, actual);

        actual = db.transact("load records");
        expected = "";
        assertEquals(expected, actual);

        actual = db.transact("load badTable");
        expected = "Error opening file: badTable.tbl";
        assertEquals(expected, actual);

        actual = db.transact("print fans");
        expected = "Lastname string,Firstname string,TeamName string\n"
                + "'Lee','Maurice','Mets'\n"
                + "'Lee','Maurice','Steelers'\n"
                + "'Ray','Mitas','Patriots'\n"
                + "'Hwang','Alex','Cloud9'\n"
                + "'Rulison','Jared','EnVyUs'\n"
                + "'Fang','Vivian','Golden Bears'";
        assertEquals(expected, actual);

        actual = db.transact("select Firstname,Lastname,TeamName from fans where Lastname >= 'Lee'");
        expected = "Firstname string,Lastname string,TeamName string\n" +
                "'Maurice','Lee','Mets'\n" +
                "'Maurice','Lee','Steelers'\n" +
                "'Mitas','Ray','Patriots'\n" +
                "'Jared','Rulison','EnVyUs'";
        assertEquals(expected, actual);

        actual = db.transact("select Mascot,YearEstablished from teams where YearEstablished > 1942");
        expected = "Mascot string,YearEstablished int\n" +
                "'Mr. Met',1962\n" +
                "'Pat Patriot',1960\n" +
                "NOVALUE,2012\n" +
                "NOVALUE,2007";
        assertEquals(expected, actual);


        actual = db.transact("create table seasonRatios as select City,Season,Wins/Losses as Ratio from teams,records");
        expected = "";
        assertEquals(expected, actual);

        actual = db.transact("print seasonRatios");
        expected = "City string,Season int,Ratio int\n" +
                "'New York',2015,1\n" +
                "'New York',2014,0\n" +
                "'New York',2013,0\n" +
                "'Pittsburgh',2015,1\n" +
                "'Pittsburgh',2014,2\n" +
                "'Pittsburgh',2013,1\n" +
                "'New England',2015,3\n" +
                "'New England',2014,3\n" +
                "'New England',2013,3\n" +
                "'Berkeley',2016,0\n" +
                "'Berkeley',2015,1\n" +
                "'Berkeley',2014,0";
        assertEquals(expected, actual);

        actual = db.transact("select City,Season,Ratio from seasonRatios where Ratio < 1");
        expected = "City string,Season int,Ratio int\n" +
                "'New York',2014,0\n" +
                "'New York',2013,0\n" +
                "'Berkeley',2016,0\n" +
                "'Berkeley',2014,0";
        assertEquals(expected, actual);

        actual = db.transact("store seasonRatios");
        expected = "";
        assertEquals(expected, actual);
    }


    @Test
    public void testErrors() {

        Database db = new Database();
        String actual, expected;
        actual = db.transact("store badTable");
        expected = "No such table: badTable";
        assertEquals(expected, actual);
    }

    @Test
    public void testWhereEqual() {
        Database db = new Database();
        String actual, expected;
        actual = db.transact("load fans");
        actual = db.transact("select Lastname from fans where Lastname == 'Lee'");
        expected = "Lastname string\n" +
                "'Lee'\n" +
                "'Lee'";
        assertEquals(expected, actual);

        actual = db.transact("load fans");
        actual = db.transact("select Lastname from fans where Lastname != 'Lee'");
        expected = "Lastname string\n" +
                "'Ray'\n" +
                "'Hwang'\n" +
                "'Rulison'\n" +
                "'Fang'";
        assertEquals(expected, actual);
    }

    @Test
    public void testQuerySuiteOne() {

    }

}
