# social-reasoner

Status for master branch:

[//]: # (this is a comment: see this link for badges using travis-CI, codecov, etc: https://github.com/mlindauer/SMAC3/blob/warmstarting_multi_model/README.md) 
![build](https://img.shields.io/badge/build-passing-green.svg?cacheSeconds=2592000) 
![test](https://img.shields.io/badge/test-passing-green.svg?cacheSeconds=2592000) 
![coverage](https://img.shields.io/badge/coverage-90%25-yellowgreen.svg?cacheSeconds=2592000) 
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3278c98625224ca5895f28623f1787a9)](https://www.codacy.com/app/ojrlopez27/social-reasoner?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ojrlopez27/social-reasoner&amp;utm_campaign=Badge_Grade)

Implementation:

![version](https://img.shields.io/badge/version-2.0-blue.svg?cacheSeconds=2592000)
![language](https://img.shields.io/badge/language-Java-yellowgreen.svg?cacheSeconds=2592000) 
![dependencies](https://img.shields.io/badge/dependencies-sent2vec-orange.svg?cacheSeconds=2592000)

## Overview

This is an implementation of a Social Reasoner component for Conversational Agents and Chatbots. This repository is organized into two folders:
- **SocialReasoner**: it contains the source code of the Social Reasoner, which uses a Spreading Activation mechanism so-called Behavior Networks (Pattie Maes, 1989, How todo the right thing). Internally, the system is organized into three packages wich correspond to the MVC architectural patter: model, view and control:
	- **model**: this package contains classes that have access to data such as user history, messages, intents, etc. as well as a blackboard system that works as a Short-Term Memory and Broadcasting mechanism.
	- **control**: this package contains controller classes for different aspects: communication (zmq), common utils (utils), emulators to tests experimental datasets (emulators), the spreading activation mechanism based on the Behavior Network model (bn), and a high-level implementation of the Social Reasoner (reasoner).
	- **view**: it contains classes in charge of the visualization of the social reasoner's dynamics.
- **Docs**: this folder contains documents and papers about the Social Reasoner. 


## How to cite our work

[SIGDIAL 2016 paper](https://www.semanticscholar.org/paper/Socially-Aware-Animated-Intelligent-Personal-Agent-Matsuyama-Bhardwaj/8571611db04df42d9ddcca39b1a3c23c11d51b6d)
```
@inproceedings{sigdial:2016,
  title = {Socially-Aware Animated Intelligent Personal Assistant Agent},
  author = {{Matsuyama}, {Yoichi} and {Bhardwaj}, {Arjun} and {Zhao}, {Ran} and {Romero}, {Oscar J.} and {Akoju}, {Sushma} and {Cassell}, {Justine}},
  booktitle = {Special Interest Group on Discourse and Dialogue},
  year          = "2016",
  pages         = "3807--3813"
}
```

[IJCAI 2017 paper](https://www.ijcai.org/proceedings/2017/532)
```
@inproceedings{ijcai:2017,
  title = {Cognitive-Inspired Conversational-Strategy Reasoner for Socially-Aware Agents},
  author = {{Romero}, {Oscar} J. and {Zhao}, {Ran} and {Cassell}, {Justine}},
  booktitle = {International Joint Conference on Artificial Intelligence},
  year          = "2017",
  pages         = "3807--3813"
}
```

[IVA 2018 paper](https://dl.acm.org/citation.cfm?id=3267880&preflayout=tabs)
```
@inproceedings{iva:2018,
  title = {SOGO: A Social Intelligent Negotiation Dialogue System},
  author = {{Zhao}, {Ran} and {Romero}, {Oscar J.} and {Rudnicky}, {Alex}},
  booktitle = {International Conference on Intelligent Virtual Agents},
  year          = "2018",
  pages         = "239--246"
}
```
