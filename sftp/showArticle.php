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

//Charger l'article du jour
if($_GET['id'] == -1)
{
	$_GET['id'] = '(SELECT ID FROM OMNI_Omnilogismes ORDER BY Sortie DESC LIMIT 1)';
}

$article = mysql_fetch_assoc(Sql::query('
SELECT
	O.ID,
	O.Titre AS T,
	O.Statut AS S,
	O.Sortie AS D,
	O.Accroche AS Q,
	DATE_FORMAT(O.Sortie, "%d/%m/%y") AS S,

	O.Omnilogisme AS O,

	A.Auteur AS A,
	A.ID AS AID

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
Typo::setTexte(utf8_decode($article['T']));
$article['T'] = utf8_encode(Typo::parseLinear());

if(isset($article['Q']))
{
	Typo::setTexte(utf8_decode($article['Q']));
	$article['Q'] = utf8_encode(Typo::parseLinear());
}

Typo::setTexte(utf8_decode($article['O']));
$article['O'] = ParseMath(utf8_encode(Typo::parse()));

// Ajouter la bannière si nécessaire
$bannerPath = '/images/Banner/' . $article['ID'] . '.png';
if(is_file(PATH . $bannerPath))
	$article['B'] = 'http://omnilogie.fr' . $bannerPath;
else
	$article['B'] = null;

//Ajouter les sources
$sources = Sql::query('SELECT Titre, URL FROM OMNI_More WHERE Reference=' . $article['ID']);
$article['U'] = array();

while($source = mysql_fetch_assoc($sources))
{
	$article['U'][] = array_map("utf8_decode", $source);
}












/**
 * "VUE" : renvoyer les données.
 */
vue($article);