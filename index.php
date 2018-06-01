<?php
header("Access-Control-Allow-Origin: *");

$api_key = "GOOGLE_API_KEY";
$yelp_key = "YELP_API_KEY";

function getGeo($address, $key) {
	$url = "https://maps.googleapis.com/maps/api/geocode/json?address=" . 
			urlencode($address) . "&key=" . $key;
	$obj = json_decode(file_get_contents($url));
	$lat = $obj->results[0]->geometry->location->lat;
	$lng = $obj->results[0]->geometry->location->lng;
	return $lat . "," . $lng;
}
function getPlaces($location, $radius, $type, $keyword, $key, $pagetoken) {
	if($radius == null || !is_numeric($radius)) {
		$radius = 10;
	}
	$radius *= 1609.3;
	$url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" . 
			$location . "&radius=" . $radius . "&type=" . $type . 
			"&keyword=" . urlencode($keyword) . "&key=" . $key . 
			"&pagetoken=" . $pagetoken;
	return file_get_contents($url);
}
function getDetails($id, $key) {
	$url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" . 
			$id . "&key=" . $key;
	return file_get_contents($url);
}
function getDirections($start, $end, $mode, $key) {
	$url = "https://maps.googleapis.com/maps/api/directions/json?origin=" . 
			urlencode($start) . "&destination=" . $end ."&mode=" . $mode . "&key=" . $key;
	return file_get_contents($url);
}
function yelpRequest($path, $url_params = array()) {
    try {
        $curl = curl_init();
        if (FALSE === $curl)
            throw new Exception("Failed to initialize");
        $url = $path . "?" . http_build_query($url_params);
        curl_setopt_array($curl, array(
            CURLOPT_URL => $url,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_ENCODING => "",
            CURLOPT_MAXREDIRS => 10,
            CURLOPT_TIMEOUT => 30,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "GET",
            CURLOPT_HTTPHEADER => array(
                "authorization: Bearer " . $GLOBALS["yelp_key"],
                "cache-control: no-cache",
            ),
        ));
        $response = curl_exec($curl);
        if (FALSE === $response)
            throw new Exception(curl_error($curl), curl_errno($curl));
        $http_status = curl_getinfo($curl, CURLINFO_HTTP_CODE);
        if (200 != $http_status)
            throw new Exception($response, $http_status);
        curl_close($curl);
    } catch(Exception $e) {
        trigger_error(sprintf(
            "Curl failed with error #%d: %s",
            $e->getCode(), $e->getMessage()),
            E_USER_ERROR);
    }
    return $response;
}
function businessMatch($name, $address, $city, $state, $country, $lat, $lng, $zip) {
	$url_params = array();
	$url_params["name"] = urlencode($name);
	$url_params["address1"] = urlencode($address);
	$url_params["city"] = urlencode($city);
	$url_params["state"] = urlencode($state);
	$url_params["country"] = urlencode($country);
	$url_params["latitude"] = $lat;
	$url_params["longitude"] = $lng;
	$url_params["zip_code"] = $zip;
	$url_params["limit"] = 3;
	
	$path = "https://api.yelp.com/v3/businesses/matches";
	
	return yelpRequest($path, $url_params);
}
function businessReviews($id) {
	$url_params = array();
	
	$path = "https://api.yelp.com/v3/businesses/" . $id . "/reviews";
	
	return yelpRequest($path, $url_params);
}

if(isset($_GET["keyword"])) {
	if(isset($_GET["location_radio"]) && $_GET["location_radio"] == "location_other") {
		$location = getGeo($_GET["location_text"], $api_key);
	} else {
		$location = $_GET["here_coord"];
	}
	echo getPlaces($location, $_GET["distance"], $_GET["category"], 
				   $_GET["keyword"], $api_key, $_GET["pagetoken"]);
} else if(isset($_GET["placeid"])) {
	$obj = json_decode(getDetails($_GET["placeid"], $api_key));
	$result = $obj->result;
	
	$name = $result->name;
	$lat = $result->geometry->location->lat;
	$lng = $result->geometry->location->lng;
	
	$components = $result->address_components;
	
	$number = "";
	$street = "";
	$city = "";
	$state = "";
	$country = "";
	$zip = "";
	
	foreach($components as $comp) {
		$types = $comp->types;
		foreach($types as $type) {
			switch($type) {
				case "street_number":
					$number = $comp->long_name;
					break;
				case "route":
					$street = $comp->long_name;
					break;
				case "locality":
					$city = $comp->long_name;
					break;
				case "administrative_area_level_1":
					$state = $comp->short_name;
					break;
				case "country":
					$country = $comp->short_name;
					break;
				case "postal_code":
					$zip = $comp->long_name;
					break;
			}
		}
	}
	
	$address = $number . $street;
	$yelp = json_decode(businessMatch($name, $address, $city, $state, $country, $lat, $lng, $zip));
	if(isset($yelp->businesses[0]) && isset($yelp->businesses[0]->id)) {
		$yelpReviews = json_decode(businessReviews($yelp->businesses[0]->id));
	} else {
		$yelpReviews = json_decode("{}");
	}
	
	$result = array("google" => $obj, "yelp" => $yelpReviews);
	echo json_encode($result);
	
} else if(isset($_GET["start"]) && isset($_GET["end"])) {
	echo getDirections($_GET["start"], $_GET["end"], $_GET["mode"], $api_key);
}
?>
