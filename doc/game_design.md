# Software Tycoon - Game Design

## Intro
Software tycoon is a software company management game with elements from idle, management and strategy games.

## Concept
Player manages one software company. The company develops a set of products. Only a certain number of products can be under development at any given time. Product slots are unlocked later into the game. Product slots can be freed by ending the support for a product. Player selects the type and features for the product. Features can be added or removed in the subsequent versions of the product. Bugs in the product are discovered both during the development and after the release. Player selects whether to focus on fixing bugs or on bringing new features in the next release. In addition to products, contracting projects can be made as well. Player controls how the company's assets are allocated to each ongoing project.

The game (or the company...) has three main properties:
*   __Money__: Money is spent on running costs of the company as well as upgrading the company. Money is gained from selling products or delivering contracting projects in time.
*   __Code Speed__: Unit is c/s (code per second). This is the amount of code the company is producing. This equals to how fast projects progress.
*   __Code Quality__: This is a factor or a relative value for how good the resulting code is. This determines the quality score of the products. For example, a value of 0.5 means that every 2 _code_ increase the quality score by 1. _Quality_ impact of assets (incl. employees) is tracked by a quality score. The _code quality_ shown to the player is
        (code quality) = (sum of quality score of all assets) / (sum of code speed impact of all assets)

Player manages company's employees and assets and how they are divided between the projects.

## Projects and Products
At first only one slot for a project is available. More slots can be unlocked from the upgrades tree.

Projects can be either developing own in-house products or contracting projects. Product development projects result in products which the company will then sell directly. Contracting jobs have a one-time payment that is made upon delivering the project.

### Products
Products are result of in-house projects. All products have a __type__ (types include _web application_, _mobile application_, _desktop application_). Some products can have _subproducts_. This means that a larger product types (like _enterprise information system_) can consist of other smaller products, which in can then be developed in their own projects. In case of a hierarchical product (i.e. a product structure) only the main product is the one that is sold. Only a few product types are available at the beginning. More can then be unlocked from the upgrades tree. In the beginning, the player selects the first type of products for the company. Further types can later be unlocked from the upgrades tree. Each type has a base complexity value.

Products also have __features__. Features together with the base complexity of the product determine the total __complexity__ of the product and thus also the time required to develop it, the price tag for the product and how well the product sells. Features are tied to the product type and not all types of products can have all the features. Features can be added and removed from a product. Features also have a level, which the player selects. Higher level features mean more complexity but in turn a more desirable product. Some features add more complexity than others when the feature level is increased. One feature that is common to all product types, and every product will automatically have, is _Core functionality_.

Products have a __quality__ score. Products have bugs. Bugs reduce the quality score until they are fixed. Bugs are detected during development and after the product has been released and deployed.

Each product project results in a __release__ of a newly released product or an update to an already released product. After a release, sales of the product in question gets a boost. Only the latest release is sold.

The sales quantity and the sales price depends on the product complexity. More complex products sell fewer numbers but cost more than less complex ones. Some products can be released as freemium, meaning that the income comes from ads. Products can also be released for free or the they can be made open source.

### Contracting Projects
Contracting projects have type and features like the in-house products, so the amount of _code_ needed to complete the project is calculated the same way as for the in-house product projects. Additionally these projects have a _quality_ requirement which also needs to be met in order to succesfully deliver the project. Contracting projects also have a _deadline_ which must be met or a possible monetary penalty will be inflicted.

### Project Tasks
Each project is divided into tasks. For product development projects, the tasks are improvements to features or completely new features, or bug fixes. For contracting projects, this is simply the list of features that need to be developed.

## Human Resources
Company needs people to do the work. Size of the company premises limit the maximum nmber of employees. Employees are managed in groups by employee types (not by individuals). Different employee types benefit the company in different ways.

New employees are hired by opening a a job position (or a number of job positions) and starting a hiring campaign. Then, applications start to come in for the position and new employees can then be hired. Number of received applications depends on company reputation, current salary for the position and the type of the hiring campaign.

### Employee Types
Different employee types benefit the company in different ways. Some types unlock new features, some enhance _code speed_, some enhance _code quality_. Employee types go with unique titles. Some employee types have a limit for how many employees of that type can exist.

Employee types:
*   __Developer Intern__: Adds +0.2 to c/s
*   __Junior Developer__: Adds +1 to c/s
*   __Developer__: Adds +2 to c/s, +1 to quality
*   __Senior Developer__: Adds +3 to c/s +1.1 to quality
*   __Software Test Engineer__: Adds +2 to quality
*   __Senior Software Test Engineer__: Adds +3 to quality
*   __Manager of Human Resources__:

## Assets
Player controls the assets of the company. These include computer hardware, company premises, coffee machines and so on.

### Premises
Company premises can be rented or owned. Rental properties have a deposit when moving to a new location and additionally a running cost. Owned properties only have a running cost. Premises affect on how many employees the company can have. Bigger premises can house more employees. More expensive and larger properties also have an effect on company's reputation.

## Upgrades
Upgrades system is presented as a tree. This essentially means that upgrades further into the tree are unlocked by first unlocking upgrades earlier in the tree.

## Boosts
Boosts are temporary effects that increase (or decrease...) the productivity of the company, income, sales, company reputation and so on. Boosts can be __fading__ or __constant__. The difference is that fading boosts have their peak effect instantly when the boost is triggered and then fades away towards the end of the duration of the boost. Constant boosts give a constant boost effect over the entire time the boost is active.

### Random Events
Random events are essentially boosts that trigger at random. They can have a positive or a negative impact.

## Money and Costs
The company has running costs that need to be paid. Company premises have a running cost (rent + running cost, or only running costs when the property is owned by the company) and salaries need to be paid. Costs are directly deducted constantly from the company's funds. Costs are displayed as negative m/s (money per second).

## Prestige System
Game has a prestige system typical to idle genre. Resetting the game earns Guru Points which can be used to unlock special upgrades.

## The UI