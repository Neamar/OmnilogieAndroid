<?php
/**
* Modèle : Articles
* But : Afficher la liste des articles
*/
$params = array(
	'id' => array(
		'regexp' => '[0-9]+',
		'default' => -1,
	),
	'titre' => array(
		'regexp' => '.*',
		'default' => '',
	),
);

include('common.php');

//Charger l'article du jour
if($_GET['titre'] == 'last')
{
	$where = 'O.ID = (SELECT ID FROM OMNI_Omnilogismes ORDER BY Sortie DESC LIMIT 1)';
} else if($_GET['titre'] == 'random')
{
	$where = '!ISNULL(Sortie) ORDER BY RAND()';
} else if($_GET['titre'] != '')
{
	$titre = mysql_real_escape_string(utf8_encode(Encoding::decodeFromGet('titre')));
	$where = '(O.Titre = "' . $titre . '" OR O.Titre = "' . $titre . '?")';
}
else
{
	$where = 'O.ID = ' . $_GET['id'];
}

$article = mysql_fetch_assoc(Sql::query('
SELECT
	O.ID,
	O.Titre AS T,
	O.Accroche AS Q,
	DATE_FORMAT(O.Sortie, "%d/%m/%y") AS S,

	O.Omnilogisme AS O,

	A.Auteur AS A,
	A.ID AS AID,
	Precedent.Titre AS Prev,
	Suivant.Titre AS Next

FROM OMNI_Omnilogismes O
LEFT JOIN OMNI_Auteurs A ON (A.ID = O.Auteur)
LEFT JOIN OMNI_Omnilogismes Precedent ON (Precedent.SuiviPar = O.ID)
LEFT JOIN OMNI_Omnilogismes Suivant ON (O.SuiviPar = Suivant.ID)
WHERE ' . $where . '
LIMIT 1'));

/**
 * "CONTRÔLEUR" : équivalent des vérifications
 */
//Vérifier la cohérence de la requête (article inconnu ?)
if(!isset($article['ID']))
	exit('Article introuvable.');

//Ajouter une vue à l'article
SQL::update('OMNI_Omnilogismes', $article['ID'],array('_NbVues'=>'NbVues+1', '_NbVuesMobile'=>'NbVuesMobile+1'));
























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


//Gérer les articles suivants / précédents
if(!is_null($article['Prev']))
{
	$article['O'] = utf8_encode('<p class="read-previous-article"><a href="' . Link::omni($article['Prev']) . '">Avant de lire cet article, assurez-vous d\'avoir lu l\'épisode précédent !</a></p>') . "\n" . $article['O'];
}
if(!is_null($article['Next']))
{
	$article['O'] = $article['O'] . "\n" . utf8_encode('<p class="read-next-article"><a href="' . Link::omni($article['Next']) . '">Cet article vous a plu ? Courez lire la suite !</a></p>') . "\n";
}
unset($article['Prev'], $article['Next']);

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