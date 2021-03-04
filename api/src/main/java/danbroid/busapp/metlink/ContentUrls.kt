package danbroid.busapp.metlink

 object ContentUrls {
  const val IPFS_GATEWAY = "https://cloudflare-ipfs.com"
  const val METLINK_KEY = "k51qzi5uqu5dim1yopj6fxfco72kpq86h23xp4pf8ayrw5angr29xyop9vk9ib"
  const val URL_STOPS_H1 = "https://h1.danbrough.org/metlink/stops.json.gz"
  const val URL_ROUTES_H1 = "https://h1.danbrough.org/metlink/routes.json.gz"

  const val URL_STOPS_GZ = "$IPFS_GATEWAY/ipns/$METLINK_KEY/stops.json.gz"
  const val URL_ROUTES_GZ = "$IPFS_GATEWAY/ipns/$METLINK_KEY/routes.json.gz"

  const val URL_STOP_DEPARTURES = "https://backend.metlink.org.nz/api/v1/stopdepartures"
}



