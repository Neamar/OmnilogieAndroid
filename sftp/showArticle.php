<?php
/**
* Modèle : Articles
* But : Afficher la liste des articles
*/
$params = array(
	'id' => array(
		'regexp' => '[0-9]+',
		'default' => -1,
	)
);

include('common.php');


$article = mysql_fetch_assoc(Sql::query('
SELECT
	O.ID,
	O.Titre,
	O.Statut,
	O.Sortie,
	O.Statut,
	O.Accroche,

	O.Omnilogisme,

	A.Auteur

FROM OMNI_Omnilogismes O
LEFT JOIN OMNI_Auteurs A ON (A.ID = O.Auteur)
WHERE O.ID = ' . $_GET['id'] . '
LIMIT 1'));

/**
 * "CONTRÔLEUR" : équivalent des vérifications
 */
//Vérifier la cohérence de la requête (article inconnu ?)
if(!isset($article['ID']))
	exit('Article introuvable.');

//Ajouter une vue à l'article
SQL::update('OMNI_Omnilogismes', $article['ID'],array('_NbVues'=>'NbVues+1'));
























/**
 * "MODÈLE" : mise en forme des données
 */

// Mettre en forme
Typo::setTexte(utf8_decode($article['Titre']));
$article['Titre'] = utf8_encode(Typo::parseLinear());

Typo::setTexte(utf8_decode($article['Accroche']));
$article['Accroche'] = utf8_encode(Typo::parseLinear());

Typo::setTexte(utf8_decode($article['Omnilogisme']));
$article['Omnilogisme'] = utf8_encode(Typo::parse());

// Ajouter la bannière si nécessaire
$bannerPath = '/images/Banner/' . $article['ID'] . '.png';
if(is_file(PATH . $bannerPath))
	$article['Banniere'] = 'http://omnilogie.fr' . $bannerPath;
else
	$article['Banniere'] = 'http://omnilogie.fr/images/Banner/Default.png';

//Ajouter les sources
$sources = Sql::query('SELECT Titre, URL FROM OMNI_More WHERE Reference=' . $article['ID']);
$article['Sources'] = array();

while($source = mysql_fetch_assoc($sources))
{
	$article['Sources'][] = $source;
}













/**
 * "VUE" : renvoyer les données.
 */
vue($article);