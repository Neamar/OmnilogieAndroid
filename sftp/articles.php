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

if(is_numeric($_GET['author_id']))
	$Where = 'WHERE !ISNULL(Sortie) AND A.ID = ' . $_GET['author_id'];
else if(isset($_GET['top']))
{
	$Where = 'JOIN (
	SELECT MONTH(Sortie) AS Mois, YEAR(Sortie) AS Annee, MAX(NbVotes) AS Max
	FROM OMNI_Omnilogismes
	GROUP BY Mois, Annee
	HAVING Max !=0
	) Votes ON (Votes.Mois = MONTH(O.Sortie) AND Votes.Annee = YEAR(O.Sortie) AND Votes.Max = O.NbVotes)';
}
else
	$Where = 'WHERE !ISNULL(Sortie)';


$articles = Sql::query('SELECT O.ID AS ID, O.Titre AS T, A.Auteur AS A, O.Accroche AS Q, DATE_FORMAT(Sortie, "%d/%m/%y") AS S
FROM OMNI_Omnilogismes O
LEFT JOIN OMNI_Auteurs A ON (A.ID = O.Auteur)
' . $Where . '
ORDER BY Sortie ' . $_GET['order'] . '
LIMIT ' . $_GET['start'] . ',' . $_GET['limit']);

while($article = mysql_fetch_assoc($articles))
{
	// Ajouter la bannière si nécessaire
	$bannerPath = '/images/Banner/Thumbs/' . $article['ID'] . '.png';
	if(is_file(PATH . $bannerPath))
		$article['B'] = 'http://omnilogie.fr' . $bannerPath;
	else
		$article['B'] = null;

	Typo::setTexte(utf8_decode($article['T']));
	$article['T'] = utf8_encode(Typo::parseLinear());

	if(isset($article['Q']))
	{
		Typo::setTexte(utf8_decode($article['Q']));
		$article['Q'] = utf8_encode(Typo::parseLinear());
	}

	$json[] = $article;
}

/**
 * "VUE"
 */
vue($json);