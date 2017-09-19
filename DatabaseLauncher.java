import db.Database;

public class DatabaseLauncher {

    public static void main(String[] args) {

        //System.out.println("Hwang".compareTo("Lee"));
        Database db = new Database();

        double start = System.currentTimeMillis();

        //String result;
        //result = db.transact("create table t2 (x sdf)");
        //result = db.transact("insert into T1 values 1.0, NaN, 2.4");
        //result = db.transact("insert into T1 values 8, 3, 1.2");
        //result = db.transact("insert into T1 values 13, 8.3");
        //result = db.transact("insert into T1 values 13, 5.2");

        //result = db.transact("select a/b as d from T1");
        //System.out.println(result);
        //System.out.println(result);
        String r = "";
        r = db.transact("load t1");
        r = db.transact("store t1");
        System.out.println(r);
        /*

        result = db.transact("create table T2 (x int, z int)");
        result = db.transact("insert into T2 values 2, 5");
        result = db.transact("insert into T2 values 8, 9");
        result = db.transact("insert into T2 values 10, 1");


        result = db.transact("create table T3 (c string, d string, e string)");
        result = db.transact("insert into T3 values \'a\', \'a\', \'a\'");
        result = db.transact("insert into T3 values \'b\', \'b\', \'b\'");
        result = db.transact("insert into T3 values \'c\', \'c\', \'c\'");
        result = db.transact("insert into T3 values \'d\', \'d\', \'d\'");


        result = db.transact("select x + y as column from T1 where column > 7");

        System.out.println(result);

        result = db.transact("load teams");
        result = db.transact("print teams");
        System.out.println(result);

        /*String result;
        result = db.transact("create table T1 (x int, y int)");
        result = db.transact("insert into T1 values 7, NOVALUE");



        result = db.transact("select x / 2.0 as g from T1");
        System.out.println(result);*/

        //String s = "        create     table    t1    (    x    int    ,    y    string    )";
        //System.out.println(s.replaceAll("\\s+", " "));


        //CLOCK RUNTIME
        double end = System.currentTimeMillis();
        System.out.println("\n---------------------------\nRun time: "
                + ((end - start) / 1000.0) + " ms");
    }

}
