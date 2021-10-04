import groovy.transform.Field
import java.util.List
import org.apache.commons.lang3.text.StrSubstitutor
@Field def Map<String,String> parameterMap = new LinkedHashMap<String, String>()
@Field def Map<String,String> ajexMap = new LinkedHashMap<String, String>()



////////////////////////////////////////
def parseInputParams(def parent,params)
{
  loadProperty(parent,params)

}


////////////////////////////////////
void loadProperty(def parent,params)
{
  for (param in params) {
        String[] paramArray = param.split(":", 2)
        String key = paramArray[0]
    println "KEY : ${key}"
        if (paramArray.length == 2) {
            String value = paramArray[1]
          println "VALUE : ${value}"
            parameterMap.put(key.toString().trim(), value.toString().trim())

        } else if (paramArray.length == 1) {
            parameterMap.put(key.toString().trim(), "")
        }
  }
  println "parameterMap : ${parameterMap}"
      
      String env = parameterMap.get("environment")
      line = env.substring(0, env.indexOf("-"))
      println "LINE : ${line}"
     //added this line to load the Test-Env//
      def TestEnv=env.substring(env.indexOf("-")+1,env.length())
      println "TestEnv : ${TestEnv}"
      def testType="ServiceTests"
      loadAjexJobMaps(line,testType,TestEnv,ajexMap,parent)
}




////////////////////////////////////////////////////////////////////////////////
// Create a new method to load Ajex JobMap with new @TESTENV parameter
 void loadAjexJobMaps(line, testType,TestEnv, testMap,def parent) {
    userDataYML = readYaml file:  'property/test.yml'
    println "userDataYML : ${userDataYML}"
    def jobData = userDataYML.(line.toString()).(testType.toString())
    def TestEnvdata = userDataYML.(line.toString()).(TestEnv.toString())
   println "jobData : ${jobData}"
   println "TestEnvdata :${TestEnvdata}"
    //jobData = (jobData == null ? "" : jobData)
   populateAjexMap(jobData,TestEnvdata, testMap)
}



//////////////////////////////////////////////////////////////////////
// Create a New method to Populate AJex Map with  @TESTENVDATA Parameter
public void populateAjexMap(jobData,TestEnvdata,jobMap) {
    def ymlOldSchema=false
    def Map<String, String> paramMap = new HashMap<String, String>();
    if (jobData != null) 
  {
    //Put TestEnvdata into ParamMap
    TestEnvdata.each
    {
     paramMap.put(it.key, it.value)
    }
        jobData.each {
            
            def jobName = it.key
            def value = it.value
            if (!ymlOldSchema) {
                value = value.JobParameter
            }
            if (value != "none") {
                value.each {
                    paramMap.put(it.key, it.value)
                }
            }
	jobMap.put(jobName, paramMap)
        }
    }
    
    
    echo "Completed job data population in map"
  println "paramMap : ${paramMap}"
  println "AjexMap : ${jobMap}"
}

