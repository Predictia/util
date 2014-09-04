package es.predictia.util.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ParserTest {

	@Test
	public void parseFileTest() throws Exception{
		NodeParserHelper usersPh = new NodeParserHelper(getResourceReader("/es/predictia/util/parser/data-users.txt"));
		@SuppressWarnings("unchecked")
		List<User> users = (List<User>) usersPh.getParsedBeans();
		Assert.assertEquals(29, users.size());
		NodeParserHelper rolesPh = new NodeParserHelper(getResourceReader("/es/predictia/util/parser/data-roles.txt"));
		@SuppressWarnings("unchecked")
		List<Role> roles = (List<Role>) rolesPh.getParsedBeans();
		Assert.assertEquals(3, roles.size());
		RelationshipParserHelper urrph = new RelationshipParserHelper(getResourceReader("/es/predictia/util/parser/data-userroles.txt"));
		urrph.fillRelationships(users, roles);
		Assert.assertNotNull(((User)users.get(0)).getRoles());
		Assert.assertTrue(((User)users.get(0)).getRoles().size()>0);
	}

	@Test
	public void parseSplitterTest() throws Exception{
		List<String> lineData1 = NodeParserHelper.getLineData("an\\\"tonio; cofino;");
		Assert.assertEquals(3, lineData1.size());
	}

	private BufferedReader getResourceReader(final String resourceLoc){
		return new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream(resourceLoc)
		));
	}
}
