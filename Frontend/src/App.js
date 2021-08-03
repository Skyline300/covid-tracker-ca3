import React, {Component} from 'react';
import type {Node} from 'react';
import {
  ActivityIndicator,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  TextInput,
  Button,
} from 'react-native';

import {
  Colors,
  DebugInstructions,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

import Header from './components/Header';

import fetchObj from './utils/api';

const Section = ({children, title}): Node => {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <View style={styles.sectionContainer}>
      <Text
        style={[
          styles.sectionTitle,
          {
            color: isDarkMode ? Colors.white : Colors.black,
          },
        ]}>
        {title}
      </Text>
      <Text
        style={[
          styles.sectionDescription,
          {
            color: isDarkMode ? Colors.light : Colors.dark,
          },
        ]}>
        {children}
      </Text>
    </View>
  );
};

const numberWithCommas = num => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

const DataGrid = ({title, data}) => {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <View style={styles.sectionContainer}>
      <Text
        style={[
          styles.sectionTitle,
          {
            color: isDarkMode ? Colors.white : Colors.black,
          },
        ]}>
        {title}: {numberWithCommas(data)}
      </Text>
    </View>
  );
};

const NewTotalGrid = ({baseTitle, newData, totalData}) => {
  return (
    <View>
      <DataGrid title={'New ' + baseTitle} data={newData} />
      <DataGrid title={'Total ' + baseTitle} data={totalData} />
    </View>
  );
};

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

class MainApp extends Component {
  state = {
    newConfirmed: 0,
    totalConfirmed: 0,
    newDeaths: 0,
    totalDeaths: 0,
    newRecovered: 0,
    totalRecovered: 0,
    isLoading: true,
    isDarkMode: false,
    countryText: '',
  };

  constructor() {
    super();
    this.onChangeCountryText = this.onChangeCountryText.bind(this);
    this.onClickSubmit = this.onClickSubmit.bind(this);
  }
  componentDidMount() {
    let apiUrl = this.props.apiUrl;
    this.apiUrl = this.props.apiUrl;
    fetchObj(apiUrl)
      .fetchData()
      .then(res => {
        const {
          newConfirmed,
          totalConfirmed,
          newDeaths,
          totalDeaths,
          newRecovered,
          totalRecovered,
        } = res;
        this.setState({
          newConfirmed,
          totalConfirmed,
          newDeaths,
          totalDeaths,
          newRecovered,
          totalRecovered,
          isLoading: false,
        });
      });
  }

  onChangeCountryText(text) {
    this.setState({
      countryText: text,
    });
  }

  onClickSubmit = country => () => {
    console.log("Searching :\t"+country);
    fetchObj(this.apiUrl)
      .fetchDataByCountry(country)
      .then(res => {
        const {
          newConfirmed,
          totalConfirmed,
          newDeaths,
          totalDeaths,
          newRecovered,
          totalRecovered,
        } = res;
        this.setState({
          newConfirmed,
          totalConfirmed,
          newDeaths,
          totalDeaths,
          newRecovered,
          totalRecovered,
        }).catch(err => console.log(err));
      });
  };

  render() {
    const darkMode = this.state.isDarkMode;
    const isLoading = this.state.isLoading;
    const {
      newConfirmed,
      totalConfirmed,
      newDeaths,
      totalDeaths,
      newRecovered,
      totalRecovered,
      countryText,
    } = this.state;
    const backgroundStyle = {
      backgroundColor: darkMode ? Colors.darker : Colors.lighter,
    };
    if (isLoading) {
      return (
        <SafeAreaView style={backgroundStyle}>
          <ActivityIndicator size="large" />
        </SafeAreaView>
      );
    } else {
      return (
        <SafeAreaView style={backgroundStyle}>
          <StatusBar barStyle={darkMode ? 'light-content' : 'dark-content'} />
          <ScrollView
            contentInsetAdjustmentBehavior="automatic"
            style={backgroundStyle}>
            <Header />
            <View>
              <TextInput
                value={countryText}
                onChangeText={this.onChangeCountryText}
              />
              <Button
                onPress={this.onClickSubmit(countryText)}
                title="Search"
                color="#841584"
              />
            </View>
            <View
              style={{
                backgroundColor: darkMode ? Colors.black : Colors.white,
              }}>
              <NewTotalGrid
                baseTitle="Confirmed"
                newData={newConfirmed}
                totalData={totalConfirmed}
              />
              <NewTotalGrid
                baseTitle="Deaths"
                newData={newDeaths}
                totalData={totalDeaths}
              />
              <NewTotalGrid
                baseTitle="Recovered"
                newData={newRecovered}
                totalData={totalRecovered}
              />
            </View>
          </ScrollView>
        </SafeAreaView>
      );
    }
  }
}
const App = config => () => {
  const backgroundStyle = {
    backgroundColor: Colors.darker,
  };
  return (
    <SafeAreaView style={backgroundStyle}>
      <MainApp apiUrl={config.apiUrl} />
    </SafeAreaView>
  );
};
export default App;
