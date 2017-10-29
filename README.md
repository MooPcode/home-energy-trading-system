# Home Energy Trading System
A home energy trading system, implemented in Java using JADE for Intelligent Systems (COS30018).

## Setting Up
The project contains all libraries needed to get started. If you do not have a JADE run configuration you can create one with the following properties:
* Main Class: `jade.Boot`
* Program Arguments: `-gui -agents home:home.HomeAgent;swinpower:retailers.LinearRetailerAgent;intellipower:retailers.HighDemandRetailerAgent;UnpredictiCo:retailers.RandomRetailerAgent;fridge:appliances.ApplianceAgent;microwave:appliances.ApplianceAgent`

## Architecture
The solution architecture consisted of three categories of entities. These were appliances, energy retailers and the home.  The home agent can be seen as the “master” agent in the system, performing much of the interaction with other agents. The home agent communicates with the appliance agents, to get their required power amounts, and communicates with the energy retailers to purchase electricity. 

## Libraries Used
* Java Agent DEvelopment Framework (http://jade.tilab.com/)
* JSON.simple v1.1.1 (https://code.google.com/archive/p/json-simple/)
