/**
 *  Critial Crash Example
 *
 *  Copyright 2019 Jonathan Porter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */


import groovy.json.*
 
definition(
	 
    name: "Critial Crash",
    namespace: "jonoporter",
    author: "Jonathan Porter",
    description: "A crash I cannot Figure Out",
    category: "Convenience",
	iconUrl: "http://cdn.device-icons.smartthings.com/Appliances/appliances11-icn@2x.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Appliances/appliances11-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Appliances/appliances11-icn@2x.png")


preferences{
	page(name: "page_mainPage")
}


 /****************************************** page_mainPage  *********************************************/




 
def page_mainPage(params){


   def funcJson = testJson2()
  
    try{

        def funcJsonEditRoot = funcJson_getFuncJsonEdit(funcJson);

        //funcJsonEditRoot crashes no matter what action you do to it except a null check
        if(funcJsonEditRoot){  trace { "its not null" }}
        //it throws here. 
        trace{ funcJsonEditRoot }
    }
    catch(ex){
        trace{ ex }
        trace{ "This will not show" }
    }
  
	return dynamicPage(name: "page_mainPage", title: "This will not render",  install: true, uninstall: true){
		section("Options"){
            paragraph "BLA BLA Bla"
 
		}
		section("Test"){
 
		}
	}
}
//some json
 def testJson2(){
    methodTrace{ "testJson()" }
	def result = 
		[func:" Thing1",funcs:[
			[func:"Thing2", name:"GOOD",funcs:[
				[func:"Thing3",funcs:[
					[func:"Thing4",funcs:[
						[func:"Thing5",funcs:[
							5,
							5
						]],
						[func:"Thing6",funcs:[
							1,
							5.5,
							100,
						]],
					]],					
					[func:"Thing7",funcs:[
						100,
						5
					]],	
					[func:"Thing8",funcs:[
						5,
						1.5
					]],		
				]],
			]],
			[func:"Thing9", name:"GOOD",funcs:[]]
		]];



	return result;
}


 
 



 /****************************************** logging  *********************************************/

 
def trace(getMessage){ 			debugPrint( 70 ,getMessage)}
def methodResult(getMessage){ 	debugPrint( 60, getMessage)}
def methodTrace(getMessage){ 	debugPrint( 50, getMessage)}
def warning(getMessage){ 		debugPrint( 40, getMessage)}
def error(getMessage){ 			debugPrint( 30 ,getMessage)}
def critical(getMessage){ 		debugPrint( 20, getMessage)}

 
def debugPrint(Integer level, getMessage ){
	//	if (enableDebugLogging&&level>loggingLevel) {
			log.debug(getMessage())	
			//println getMessage()
//	}
}
/****************************************** funcJson *********************************************/
//the json the rule will be stored as the name and the a parameter functions. any extra parameters will be 
//stored as other keys. 
def funcJson_ctor(){
	return [
		func:"", 
		funcs:[ ]
	];
}

def funcJsonEdit_ctor(){
	return [
		parent: null,
		line: -1,
		funcJson: null,
		children: [],
		parameterIndex: -1,
	];
}
def funcJsonEditContext_ctor(){
	return [
		root : null,
		line : null,
		item : null,
	];
}
 
def funcJson_getFuncJsonEdit(funcJson,Map parent = null,Integer line = 0,Integer parameterIndex = -1){
    methodTrace{ "funcJson_getFuncJsonEdit(funcJson,parent,${line},${parameterIndex})" }
    
	def result = funcJsonEdit_ctor() <<  [
		funcJson: 	funcJson,
		parent: 	parent,
		parameterIndex: parameterIndex,
		line: 		line,
	];
	line++;
    result.children = [];
    if(funcJson instanceof Map  && (funcJson.funcs) && funcJson.funcs.size()>0){
        for(Integer index in 0..(funcJson.funcs.size()-1)){ //originally did a collect with index but switched in looking for solution
            def child = funcJson.funcs[index];
            def subItem = funcJson_getFuncJsonEdit(child,result,line,index );
            result.children.add(subItem);
        }
    }
	return result;
}
 

def funcJson_fromFuncJsonEdit( funcJsonEdit )
{
    methodTrace{ "funcJson_fromFuncJsonEdit(funcJsonEdit)" }
    if(funcJsonEdit.funcJson  instanceof Map ){
	def result = funcJson_ctor() << funcJsonEdit.funcJson  << [
			funcs: funcJsonEdit.children.collect { child->  funcJson_fromFuncJsonEdit( child) }
		];
    }
    else{
        result = funcJsonEdit.funcJson;
    }
	return result;
}

