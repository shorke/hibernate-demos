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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Testing multiple bi-directional ManyToOne-relationships, where the
 * relationship propery of the owner side has the same name
 *
 */
public class ManyToOneTest {

    private static EntityManagerFactory entityManagerFactory;

    @BeforeClass
    public static void setUpEntityManagerFactory() {
	entityManagerFactory = Persistence.createEntityManagerFactory("hikePu");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
	entityManagerFactory.close();
    }

    @Test
    public void canPersistAndLoadPersonAndHikesAndRaces() {
	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	entityManager.getTransaction().begin();

	Person bob = new Person("Bob", "McRobb");
	Hike cornwall = new Hike("Visiting Land's End", new Date(),
		new BigDecimal(5));

	Race seasonsFirst = new Race("Seasons first race", new Date(),
		new BigDecimal(5));

	// let Bob organize the two hikes
	cornwall.setOrganizer(bob);
	bob.getOrganizedHikes().add(cornwall);

	// let Bob organize the seasons first race
	seasonsFirst.setOrganizer(bob);
	bob.getOrganizedRaces().add(seasonsFirst);

	// persist organizer (will be cascaded to hikes)
	entityManager.persist(bob);

	entityManager.getTransaction().commit();
	entityManager.clear();

	// load it back
	entityManager.getTransaction().begin();

	Person loadedPerson = entityManager.find(Person.class, bob.getId());
	assertThat(loadedPerson).isNotNull();
	assertThat(loadedPerson.getFirstName()).isEqualTo("Bob");
	assertThat(loadedPerson.getOrganizedHikes()).onProperty("description")
		.containsOnly(cornwall.getDescription());
	assertThat(loadedPerson.getOrganizedRaces()).onProperty("description")
		.containsOnly(seasonsFirst.getDescription());

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    @Test
    public void canPersistAndLoadPersonAndHikes() {
	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	entityManager.getTransaction().begin();

	Person bob = new Person("Bob", "McRobb");
	Hike cornwall = new Hike("Visiting Land's End", new Date(),
		new BigDecimal(5));

	// let Bob organize the two hikes
	cornwall.setOrganizer(bob);
	bob.getOrganizedHikes().add(cornwall);

	// persist organizer (will be cascaded to hikes)
	entityManager.persist(bob);

	entityManager.getTransaction().commit();
	entityManager.close();

	// load it back
	entityManager = entityManagerFactory.createEntityManager();
	entityManager.getTransaction().begin();

	Person loadedPerson = entityManager.find(Person.class, bob.getId());
	assertThat(loadedPerson).isNotNull();
	assertThat(loadedPerson.getFirstName()).isEqualTo("Bob");
	assertThat(loadedPerson.getOrganizedHikes()).onProperty("description")
		.containsOnly(cornwall.getDescription());

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    @Test
    public void canPersistAndLoadPersonAndRaces() {
	EntityManager entityManager = entityManagerFactory
		.createEntityManager();

	entityManager.getTransaction().begin();

	Person bob = new Person("Bob", "McRobb");

	Race seasonsFirst = new Race("Seasons first race", new Date(),
		new BigDecimal(5));

	// let Bob organize the seasons first race
	seasonsFirst.setOrganizer(bob);
	bob.getOrganizedRaces().add(seasonsFirst);

	// persist organizer (will be cascaded to hikes)
	entityManager.persist(bob);

	entityManager.getTransaction().commit();
	entityManager.close();

	// load it back
	entityManager = entityManagerFactory.createEntityManager();
	entityManager.getTransaction().begin();

	Person loadedPerson = entityManager.find(Person.class, bob.getId());
	assertThat(loadedPerson).isNotNull();
	assertThat(loadedPerson.getFirstName()).isEqualTo("Bob");
	assertThat(loadedPerson.getOrganizedRaces()).onProperty("description")
		.containsOnly(seasonsFirst.getDescription());

	entityManager.getTransaction().commit();
	entityManager.close();
    }

}
