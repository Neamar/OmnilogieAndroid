<?php
mysql_set_charset('utf8');

$json = array();

foreach($params as $name => $param)
{
	if(!isset($_GET[$name]))
		$_GET[$name] = $param['default'];

	if(!preg_match('`' . $param['regexp'] . '`', $_GET[$name]))
		Debug::fail('Paramètre défini de façon incorrecte : ' . $name);
}

function vue($json)
{
	if(isset($_GET['readable']))
	{
		header('Content-Type:text/html; charset=UTF-8');
		echo '<pre>';
		ob_start();
		print_r($json);
		echo htmlentities(ob_get_clean());
	}
	else
	{
		echo json_encode($json);
	}

	exit();
}