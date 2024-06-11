# kata-tracker

Track state and trend of your kata/dojo workflow. 

#### TL;DR
This once will become a maven-central-hosted junit-extension. Until then the easiest way to use it is: 
- Check out this project
- Start the CounterDisplay or the TimeSeriesChart (or both)
- Do a kata and annotate your test class with @RedGreenTracking
- Have fun :)

Heavily inspired by/based on ideas of [Llewellyn Falco](https://github.com/isidore) (the tracker: https://github.com/LearnWithLlew/ExtremeFakeItTillYouMakeIt.Java) 
and [Nitsan Avni](https://github.com/nitsanavni) (the chart/graph https://github.com/nitsanavni/katas/tree/main/shortest-longest-red). 

Seen at our first [Samman Society](https://www.sammancoaching.org/) Unconference at Vienna in 2024. 

At the moment it consists of three parts: 
- JUnit extension that sends the result of tests to a MQTT broker. 
- Tracker that counts green/red. The UI code was copies from Llew but instead of reading from file it gets its values by subscribing to the mqtt broker. Also the UI plays the red/green sound files by itself, they also were included what Llew did. 

![tracker](https://raw.githubusercontent.com/pfichtner/kata-tracker/main/src/test/resources/org/counterdisplay/CounterDisplayTest.oneToTwo.Linux.approved.png)
- Graph that shows the red/green timeline and some statistics such as "longest time in red". The values are also retrieved by subscribing to the mqtt broker. 

![chart](https://raw.githubusercontent.com/pfichtner/kata-tracker/main/src/test/resources/com/github/pfichtner/samman/kata/chart/TimeSeriesChartTest.withData.Linux.approved.png)

The UIs start and stop a MQTT broker within the JVM if there is no server running on port 1883 (default mqtt port)

...more to come


Caveats: 
- If you run multiple tests, each result is published, so gren, red, green, green test results in four tests would count as as very short red phase

Ideas: 
- Replace MQTT by HTTP (PUT and GET with long pollling (HTTP PUT shouldn't be slower than MQTT pub)
  - We then could keep track of the states withing the HTTP server
  - We can draw graphs there as well
  - And we could add a control panel with reset, ... there
