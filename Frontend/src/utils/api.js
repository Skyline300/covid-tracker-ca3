const fetchData = async baseUrl => {
  let response = await fetch(baseUrl);
  let jsonData = await response.json();
  return jsonData;
};

const fetchDataByCountry = baseUrl => async country => {
  let response = await fetch(baseUrl + `/${country}`);
  let jsonData = await response.json();
  return jsonData;
};

module.exports = baseUrl => ({
  fetchData: () => fetchData(baseUrl),
  fetchDataByCountry: country => fetchDataByCountry(baseUrl)(country),
});
