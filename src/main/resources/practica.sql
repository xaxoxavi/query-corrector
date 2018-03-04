#HINTS:https://rua.ua.es/dspace/bitstream/10045/28357/11/T11_Conjuntos.pdf

#Esbrinar el títol i ladescripció d’aquelles pel·lícules que es van rodar en l’idioma original que té per nom (name) “English”.(FK -> filme.language_id)
#@
SELECT pollo.title, pollo.description FROM film as pollo INNER JOIN
              language ON pollo.language_id=language.language_id
              WHERE language.name = 'English';
#$
# Esbrinar títol i descripció d’aquelles pel·lícules que pertanyen a la categoria “Sci-Fi”.
#@
SELECT DISTINCT film.title, film.description FROM film INNER JOIN film_category ON film_category.film_id = film.film_id
											           INNER JOIN category ON category.category_id = film_category.category_id
													   WHERE category.name = "Sci-Fi";
#$

# Esbrinar el ID, el nom i el cognom (last_name i first_name) dels actors que no han participat a cap pel·lícula,
# més aquells que han participat a menys de 20 pel·lícules.
#@
SELECT actor.* FROM actor LEFT JOIN film_actor ON actor.actor_id = film_actor.actor_id
WHERE film_actor.actor_id is null
UNION ALL
SELECT actor.actor_id, actor.first_name, actor.last_name, count(film_actor.film_id) 
FROM actor
INNER JOIN film_actor ON actor.actor_id = film_actor.actor_id
GROUP BY actor.actor_id
HAVING count(film_actor.film_id) < 20;
#$
# Obtenir un llistat on aparegui els títols de les pel·lícules i el número de vegades que han estat llogades (rental) durant tot l’any 2005.
#@
SELECT film.film_id,title,count(rental.rental_id) FROM film 
         INNER JOIN inventory ON film.film_id = inventory.film_id
         INNER JOIN rental ON inventory.inventory_id = rental.inventory_id
         WHERE rental_date BETWEEN '2005-01-01' and '2005-12-31'
         GROUP BY film.film_id;
#$
#Obtenir un llistat on aparegui el id de la pel·lícula, el títol, el id del lloguer i els dies que s'ha llogat le pel·lícula (retrun-date - rental_date )  
# de totes les pel·lícules que no estan llogades actualment.
#@
SELECT film.film_id, film.title, rental.rental_id, datediff(rental.return_date,rental.rental_date) as diff
         FROM film 
         INNER JOIN inventory ON film.film_id = inventory.film_id
         INNER JOIN rental ON inventory.inventory_id = rental.inventory_id
         WHERE rental.return_date is not null;
#$
         
#Obtenir un llistat on aparegui almenys, el nom de l’actor (first_name) i la mitjana del temps de lloguer (retrun-date - rental_date )  
# de totes les pel·lícules en las que han participat. ALERTA! Pot esser que dos actors diferents tenguin el mateix nom.
#@
SELECT actor.actor_id,actor.first_name,  avg( datediff(rental.return_date,rental.rental_date)) as diff
         FROM film 
         INNER JOIN inventory ON film.film_id = inventory.film_id
         INNER JOIN rental ON inventory.inventory_id = rental.inventory_id
		 INNER JOIN film_actor ON film.film_id = film_actor.film_id
         INNER JOIN actor ON film_actor.actor_id = actor.actor_id
		 WHERE rental.return_date is not null
         GROUP BY actor.actor_id;
#$
#Esbrinar el títol i la descripció d’aquelles pel·lícules que estan únicament a l’estoc de les tendes que es troben a les adreces amb ID 3 i 4.
#https://www.techonthenet.com/mysql/intersect.php
#@
SELECT DISTINCT film.film_id,film.title, film.description FROM  store
         INNER JOIN inventory ON inventory.store_id = store.store_id
         INNER JOIN film on film.film_id = inventory.film_id
         WHERE address.address_id = 3 AND film.film_id IN (

			SELECT DISTINCT film.film_id FROM store
			INNER JOIN inventory ON inventory.store_id = store.store_id
			INNER JOIN film on film.film_id = inventory.film_id
			WHERE address.address_id = 4
         );
#$
#De totes les tendes que es troben al codi postal 07009 treure una llistat que contengui el ID de la tenda,el nom del client, el cognom del client,
#el correu electrònic del client i la quantitat total que han pagat en concepte de lloguer de pel·lícules,
#però només d’aquelles pel·lícules que durin 120 minuts o més (camp lenght).

#@
SELECT store.store_id, customer.first_name, customer.last_name, customer.email, sum(payment.amount)  FROM address
		 INNER JOIN store ON address.address_id = store.address_id
         INNER JOIN inventory ON inventory.store_id = store.store_id
         INNER JOIN rental ON rental.inventory_id = inventory.inventory_id
         INNER JOIN customer ON customer.customer_id = rental.customer_id
         INNER JOIN payment ON payment.rental_id = rental.rental_id
         INNER JOIN film ON film.film_id = inventory.film_id
         WHERE address.postal_code = '07009' and film.length >= 120
         GROUP BY store.store_id,customer.customer_id;

#$
#Mostrar els camps address i address2 de les tendes que almenys tenguin al seu estoc les pel·lícules amb títol:
# “DETECTIVE VISION” i “DEVIL DESIRE”. Si les tendes tenen més pel·lícules al seu estoc no hi ha problema, podran aparèixer al resultat.
#@
SELECT DISTINCT address.address, address.address2 FROM inventory i 
		 INNER JOIN store ON i.store_id = store.store_id
         INNER JOIN address ON address.address_id = store.address_id
		 WHERE EXISTS ( 		
SELECT film.title FROM film
         WHERE film.title IN ('DETECTIVE VISION','DEVIL DESIRE') AND i.film_id = film.film_id);
#$