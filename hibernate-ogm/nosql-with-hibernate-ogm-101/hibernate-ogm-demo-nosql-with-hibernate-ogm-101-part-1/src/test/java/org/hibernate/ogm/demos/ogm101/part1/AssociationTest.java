/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.demos.ogm101.part1;

import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Modified version of HikeTest to test neo4j associations
 *
 */
public class AssociationTest {

    private static EntityManagerFactory entityManagerFactory;
    private Person bob = new Person("Bob", "McRobb");
    private Hike cornwall = new Hike("Visiting Land's End", new Date(),
	    new BigDecimal(5));
    private Hike isleOfWight = new Hike("Exploring Carisbrooke Castle",
	    new Date(), new BigDecimal(5));

    @BeforeClass
    public static void setUpEntityManagerFactory() {
	entityManagerFactory = Persistence.createEntityManagerFactory("hikePu");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
	entityManagerFactory.close();
    }

    @Before
    public void canPersistAndLoadPersonAndHikes() {
	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	entityManager.getTransaction().begin();

	// let Bob organize the two hikes
	cornwall.setOrganizer(bob);
	bob.getOrganizedHikes().add(cornwall);

	isleOfWight.setOrganizer(bob);
	bob.getOrganizedHikes().add(isleOfWight);

	// persist organizer (will be cascaded to hikes)
	entityManager.persist(bob);

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    @Test
    public void loadPersonUsingId() {

	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	// load it back
	entityManager.getTransaction().begin();

	Person loadedPerson = entityManager.find(Person.class, bob.getId());
	assertThat(loadedPerson).isNotNull();
	assertThat(loadedPerson.getFirstName()).isEqualTo("Bob");
	assertThat(loadedPerson.getOrganizedHikes()).onProperty("description")
		.containsOnly("Visiting Land's End",
			"Exploring Carisbrooke Castle");

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    @Test
    public void loadPersonUsingPropertyJPQL() {

	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	// load it back
	entityManager.getTransaction().begin();

	Person loadedPerson = entityManager
		.createQuery("SELECT P From Person P WHERE id = :id",
			Person.class).setParameter("id", bob.getId())
		.getSingleResult();
	assertThat(loadedPerson).isNotNull();
	assertThat(loadedPerson.getFirstName()).isEqualTo("Bob");
	assertThat(loadedPerson.getOrganizedHikes()).onProperty("description")
		.containsOnly("Visiting Land's End",
			"Exploring Carisbrooke Castle");

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    @Test
    public void loadHikeUsingId() {
	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	// load it back
	entityManager.getTransaction().begin();

	Hike loadedHike = entityManager.find(Hike.class, cornwall.getId());

	assertThat(loadedHike).isNotNull();
	assertThat(loadedHike.getOrganizer()).isNotNull();
	assertThat(loadedHike.getOrganizer().getFirstName()).isEqualTo("Bob");

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    @Test
    public void loadHikeUsingPropertyCypher() {
	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	// load it back
	entityManager.getTransaction().begin();

	Hike loadedHike = (Hike) entityManager.createNativeQuery(
		"MATCH ( n:Hike { id:'" + cornwall.getId() + "' } ) " + "\n"
			+ "RETURN n", Hike.class).getSingleResult();

	assertThat(loadedHike).isNotNull();
	assertThat(loadedHike.getOrganizer()).isNotNull();
	assertThat(loadedHike.getOrganizer().getFirstName()).isEqualTo("Bob");

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    @Test
    public void loadHikeUsingPropertyJPQL() {
	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	// load it back
	entityManager.getTransaction().begin();

	Hike loadedHike = entityManager
		.createQuery("SELECT H From Hike H WHERE id = :id", Hike.class)
		.setParameter("id", cornwall.getId()).getSingleResult();

	assertThat(loadedHike).isNotNull();
	assertThat(loadedHike.getOrganizer()).isNotNull();
	assertThat(loadedHike.getOrganizer().getFirstName()).isEqualTo("Bob");

	entityManager.getTransaction().commit();
	entityManager.close();
    }

}
