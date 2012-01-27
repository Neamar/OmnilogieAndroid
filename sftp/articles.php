<?php
/**
* Modèle : Articles
* But : Afficher la liste des articles
*/
$params = array(
	'limit' => array(
		'regexp' => '[0-9]+',
		'default' => 20,
	),
	'start' => array(
		'regexp' => '[0-9]+',
		'default' => 0
	),
	'order' => array(
		'regexp' => '(asc|desc)',
		'default' => 'desc'
	),
	'author_id' => array(
		'regexp' => '[0-9]*',
		'default' => ''
	)
);

include('common.php');


$articles = Sql::query('SELECT O.ID AS ID, O.Titre AS T, A.Auteur AS A, O.Accroche AS Q
FROM OMNI_Omnilogismes O
LEFT JOIN OMNI_Auteurs A ON (A.ID = O.Auteur)
WHERE !ISNULL(Sortie)
' . (is_numeric($_GET['author_id']) ? 'AND A.ID = ' . $_GET['author_id']:'') . '
ORDER BY Sortie ' . $_GET['order'] . '
LIMIT ' . $_GET['start'] . ',' . $_GET['limit']);

while($article = mysql_fetch_assoc($articles))
{
	// Ajouter la bannière si nécessaire
	$bannerPath = '/images/Banner/Thumbs/' . $article['ID'] . '.png';
	if(is_file(PATH . $bannerPath))
		$article['B'] = 'http://omnilogie.fr' . $bannerPath;
	else
		$article['B'] = 'http://omnilogie.fr/images/Banner/Thumbs/Default.png';

	Typo::setTexte(utf8_decode($article['T']));
	$article['T'] = utf8_encode(Typo::parseLinear());

	Typo::setTexte(utf8_decode($article['Q']));
	$article['Q'] = utf8_encode(Typo::parseLinear());

	$json[] = $article;
}

/**
 * "VUE"
 */
vue($json);