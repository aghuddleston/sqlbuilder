package ca.krasnay.sqlbuilder;

import static ca.krasnay.sqlbuilder.Predicates.*;

import java.util.Arrays;
import junit.framework.TestCase;


public class SubQueryPredicateTest extends TestCase {
	
	public void testInSubQuery() {
		
        SelectCreator sc = new SelectCreator()
        .column("e.*")
        .from("Emp e")
        .where(inSubQuery("e.department ")
        	.column("d.department")
        	.from("Dep d")
        	.where("d.type='retail'"));
        
        assertEquals("select e.* from Emp e where e.department  in (select d.department from Dep d where d.type='retail')", sc.getBuilder().toString());
	}
	
	public void testInSubQueryWithPred() {
		
        SelectCreator sc = new SelectCreator()
        .column("e.*")
        .from("Emp e")
        .where(inSubQuery("e.department ")
        	.column("d.department")
        	.from("Dep d")
        	.where(in("d.type", Arrays.asList("retail", "popup"))));
        
        assertEquals("select e.* from Emp e where e.department  in (select d.department from Dep d where d.type in (:param0, :param1))", sc.getBuilder().toString());
        assertEquals("retail", sc.getPreparedStatementCreator().getParameterMap().get("param0"));
        assertEquals("popup", sc.getPreparedStatementCreator().getParameterMap().get("param1"));
	}
	

}
