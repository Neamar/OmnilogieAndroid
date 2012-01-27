<?php
/**
* Modèle : Articles
* But : Afficher la liste des articles
*/
$params = array(
);

include('common.php');


$auteurs = Sql::query('SELECT A.ID AS ID, A.Auteur AS A, COUNT(*) AS N
FROM OMNI_Auteurs A
LEFT JOIN OMNI_Omnilogismes O ON O.Auteur = A.ID
WHERE !ISNULL(O.Sortie)
GROUP BY A.ID
ORDER BY COUNT(*) DESC');

while($auteur = mysql_fetch_assoc($auteurs))
{
	$json[] = $auteur;
}

/**
 * "VUE"
 */
vue($json);