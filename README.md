# social-reasoner

Implementation of a Social Reasoner component for the SARA (Socially-Aware Robotic Assistant) system. This repository is organized into three folders:
- Execution: it contains the results of testing the Social Reasoner using a dataset from WEF (WOrld Economic Forum) event at Tiajin 2016.
- SocialReasoner: it contains the implementation of the Social Reasoner by using a Spreading Activation so-clled Behavior Networks (Pattie Maes, 1989, How todo the right thing). Internally, the system is organized int three packages wich correspond with the MVC architectural patter: model, view and control:
	- model: this package contains classes that have access to data such as user history, messages, intents, etc. as well as the blackboard component that works as a Short-Term Memory mechanism.
	- control: this package contains controller classes for different aspects: communication (vht), common utils (utils), emulators to tests Tianjing and Davos datasets (emulators), the spreading activation mechanism based on the Behavior Network model (bn), and a hish-level implementation of the Social Reasoner (reasoner).
	- view: it contains classes in charge of the visualization of the social reasoner's dynamics.
- SocialReasoner-MUF: this is a simplified version of SocialReasoner (no emulators, no datasets, no visualization, etc.) intended to work over MUF	
- Docs: this folder contains documents and papers about the Social Reasoner. 


