#!/usr/bin/env python2.7
#coding=utf-8
import requests
import json
from conf.environ import get_config
from conf.oozie import OozieConf
from lib.log import MyLogger



class HTTPMethod():

    '''
    HTTPMethod
    '''

    GET = 1
    PUT = 2
    POST = 3
    DELETE = 4

class HTTPParam():

    def __init__(self, name, mandatory):
        self.name=name
        self.mandatory=mandatory

class RSWebClient(object):
    '''
    Restful API client
    '''

    FUNC_MAP = {
                HTTPMethod.GET:     requests.get,
                HTTPMethod.POST:    requests.post,
                HTTPMethod.PUT:     requests.put,
                HTTPMethod.DELETE:  requests.delete
    }

    def __init__(self, url, version):
        self.url = url
        self.version = version

        #self.args = []
        #self.content = None
        self.logger = MyLogger.getLogger()


    def _buildQuery(self):
        if self.version is None:
            self.logger.error("Wrong version")
            return None
        else:
            self.url =str.format("{0}/{1}", self.url, self.version)

        if self.api is None:
            self.logger.error("Error api invoke")
            return None
        else:
            self.url = str.format("{0}/{1}", self.url, self.api)



    def _buildParams(self):
        self.params = {}
        if len(self.args) != 0:
            for arg in self.args:
                if not isinstance(arg, HTTPParam):
                    self.logger.error("Wrong arg type")
                    return None
                try:
                    val = getattr(self, arg.name)
                    if val is None:
                        if arg.mandatory:
                            self.logger.error("Argument %s is Mandatory with empty value" % arg)
                    else:
                        self.params[arg.name] = val

                except Exception, ex:
                    self.logger.error(ex)
                    self.logger.error("Exception when build query")

    def invoke(self):
        '''
        invoke the oozie rest api
        :return:
        '''
        self._buildQuery()
        self._buildParams()
        try:
            if self.url is None or self.url == '':
                return None

            if self.method is None:
                self.mehtod = HTTPMethod.GET

            if self.payload is None:
                resp = self.FUNC_MAP[self.method](self.url, params=self.params, headers=self.header)
            else:
                resp = self.FUNC_MAP[self.method](self.url, params=self.params, headers=self.header, data=json.dumps(self.payload))

            if resp.status_code != 200:
                self.logger.error("Invoke oozie webservice request failed with error code= %s" % resp.status_code)
                return None

            return resp

        except Exception, ex:
            self.logger.error(ex)
            self.logger.error("Erro occurred when invoke oozie webservice request, error msg=%s", ex.message)
            return None



class OozieRSClientV1(RSWebClient):
    '''
    Oozie Restful API client
    '''

    def __init__(self):
        super(OozieRSClientV1, self).__init__(get_config(OozieConf, 'OOZIE_URL'), 'v1')



class RetrieveJobInfo(OozieRSClientV1):
    '''
    RetrieveJobInfo

    Job Information

        A HTTP GET request retrieves the job information.

        Request:

        GET /oozie/v1/job/job-3?show=info&timezone=GMT


    Job Log

        An HTTP GET request retrieves the job log.

        Request:

        GET /oozie/v1/job/job-3?show=log
        Response:

        HTTP/1.1 200 OK
        Content-Type: text/plain;charset=UTF-8

    Job Application Definition

        A HTTP GET request retrieves the workflow or a coordinator job definition file.

        Request:

        GET /oozie/v1/job/job-3?show=definition

    Job graph

        An HTTP GET request returns the image of the workflow DAG (rendered as a PNG image).

        The nodes that are being executed are painted yellow
        The nodes that have successfully executed are painted green
        The nodes that have failed execution are painted red
        The nodes that are yet to be executed are pained gray
        An arc painted green marks the successful path taken so far
        An arc painted red marks the failure of the node and highlights the error action
        An arc painted gray marks a path not taken yet
        Request:

        GET /oozie/v1/job/job-3?show=graph[&show-kill=true]
        Response:

        HTTP/1.1 200 OK
        Content-Type: image/png
        Content-Length: {image_size_in_bytes}{image_bits}
        The optional show-kill parameter shows kill node in the graph. Valid values for this parameter are 1 , yes , and true . This parameter has no effect when workflow fails and the failure node leads to the kill node; in that case kill node is shown always.

        The node labels are the node names provided in the workflow XML.

        This API returns HTTP 400 when run on a resource other than a workflow, viz. bundle and coordinator.

    '''
    class InfoType():

        INFO = 'info'
        LOG = 'log'
        GRAPH = 'graph'
        DEFINITION = 'definition'

    def __init__(self, jobid, show, timezone):
        super(RetrieveJobInfo, self).__init__()
        self.header = None
        self.payload = None
        self.jobid = jobid
        self.api = 'job/%s' % self.jobid
        self.args = [
                        HTTPParam("show", True),
                        HTTPParam("timezone", False)
                    ]

        self.method = HTTPMethod.GET
        self.show = show
        self.timezone=timezone



class RetrieveAllJobsInfo(OozieRSClientV1):
    '''

    Jobs Information

        A HTTP GET request retrieves workflow and coordinator jobs information.

        Request:

        GET /oozie/v1/jobs?filter=user%3Dbansalm&offset=1&len=50&timezone=GMT
        Note that the filter is URL encoded, its decoded value is user=bansalm .


        No action information is returned when querying for multiple jobs.

        The syntax for the filter is

        [NAME=VALUE][;NAME=VALUE]*
        Valid filter names are:

        name: the application name from the workflow/coordinator/bundle definition
        user: the user that submitted the job
        group: the group for the job
        status: the status of the job
        The query will do an AND among all the filter names.

        The query will do an OR among all the filter values for the same name. Multiple values must be specified as different name value pairs.

        Additionally the offset and len parameters can be used for pagination. The start parameter is base 1.

        Moreover, the jobtype parameter could be used to determine what type of job is looking for. The valid values of job type are: wf , coordinator or bundle .

    '''

    def __init__(self, jobtype=None, filter=None, offset=None, len=None, timezone=None):
        super(RetrieveAllJobsInfo, self).__init__()
        self.header = None
        self.payload = None
        self.api = 'jobs'
        self.args = [
                        HTTPParam("jobtype", False),
                        HTTPParam("filter", False),
                        HTTPParam("offset", False),
                        HTTPParam("len", False),
                        HTTPParam("timezone", False)
                    ]

        self.method = HTTPMethod.GET
        self.jobtype = jobtype
        self.filter = filter
        self.offset = offset
        self.len = len
        self.timezone = timezone


class JobManagementRequest(OozieRSClientV1):

    '''

    A HTTP PUT request starts, suspends, resumes, kills, or dryruns a job.

    Request:

    PUT /oozie/v1/job/job-3?action=start
    Response:

    HTTP/1.1 200 OK
    Valid values for the 'action' parameter are 'start', 'suspend', 'resume', 'kill', 'dryrun', 'rerun', and 'change'.

    Rerunning and changing a job require additional parameters, and are described below:

Re-Runing a Workflow Job

    A workflow job in SUCCEEDED , KILLED or FAILED status can be partially rerun specifying a list of workflow nodes to skip during the rerun. All the nodes in the skip list must have complete its execution.

    The rerun job will have the same job ID.

    A rerun request is done with a HTTP PUT request with a rerun action.

    Request:

    PUT /oozie/v1/job/job-3?action=rerun
    Content-Type: application/xml;charset=UTF-8
    .
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <property>
            <name>user.name</name>
            <value>tucu</value>
        </property>
        <property>
            <name>oozie.wf.application.path</name>
            <value>hdfs://foo:8020/user/tucu/myapp/</value>
        </property>
        <property>
            <name>oozie.wf.rerun.skip.nodes</name>
            <value>firstAction,secondAction</value>
        </property>
        ...
    </configuration>
    Response:

    HTTP/1.1 200 OK

----------------------------------------------------------

Re-Runing a coordinator job

        A coordinator job in RUNNING SUCCEEDED , KILLED or FAILED status can be partially rerun by specifying the coordinator actions to re-execute.

        A rerun request is done with an HTTP PUT request with a coord-rerun action .

        The type of the rerun can be date or action .

        The scope of the rerun depends on the type: * date : a comma-separated list of date ranges. Each date range element is specified with dates separated by :: * action : a comma-separated list of action ranges. Each action range is specified with two action numbers separated by -

        The refresh parameter can be true or false to specify if the user wants to refresh an action's input and output events.

        The nocleanp paramter can be true or false to specify is the user wants to cleanup output events for the rerun actions.

        Request:

        PUT /oozie/v1/job/job-3?action=coord-rerun&type=action&scope=1-2&refresh=false&nocleanup=false
        .
        or

        PUT /oozie/v1/job/job-3?action=coord-rerun&type=date2009-02-01T00:10Z::2009-03-01T00:10Z&scope=&refresh=false&nocleanup=false
        .
        Response:

        HTTP/1.1 200 OK


---------------------------------------------------------

    Re-Runing a bundle job

        A coordinator job in RUNNING SUCCEEDED , KILLED or FAILED status can be partially rerun by specifying the coordinators to re-execute.

        A rerun request is done with an HTTP PUT request with a bundle-rerun action .

        A comma separated list of coordinator job names (not IDs) can be specified in the coord-scope parameter.

        The date-scope parameter is a comma-separated list of date ranges. Each date range element is specified with dates separated by :: . If empty or not included, Oozie will figure this out for you

        The refresh parameter can be true or false to specify if the user wants to refresh the coordinator's input and output events.

        The nocleanp paramter can be true or false to specify is the user wants to cleanup output events for the rerun coordinators.

        Request:

        PUT /oozie/v1/job/job-3?action=bundle-rerun&coord-scope=coord-1&refresh=false&nocleanup=false
        .
        Response:

        HTTP/1.1 200 OK
        Changing endtime/concurrency/pausetime of a Coordinator Job

        A coordinator job not in KILLED status can have it's endtime, concurrency, or pausetime changed.

        A change request is done with an HTTP PUT request with a change action .

        The value parameter can contain any of the following: * endtime: the end time of the coordinator job. * concurrency: the concurrency of the coordinator job. * pausetime: the pause time of the coordinator job.

        Multiple arguments can be passed to the value parameter by separating them with a ';' character.

        If an already-succeeded job changes its end time, its status will become running.

        Request:

        PUT /oozie/v1/job/job-3?action=change&value=endtime=2011-12-01T05:00Z
        .
        or

        PUT /oozie/v1/job/job-3?action=change&value=concurrency=100
        .
        or

        PUT /oozie/v1/job/job-3?action=change&value=pausetime=2011-12-01T05:00Z
        .
        or

        PUT /oozie/v1/job/job-3?action=change&value=endtime=2011-12-01T05:00Z;concurrency=100;pausetime=2011-12-01T05:00Z
        .
        Response:

        HTTP/1.1 200 OK


    '''

    def __init__(self, jobid, action=None, type=None, scope=None, refresh=None, nocleanup=None, value=None):
        super(JobManagementRequest, self).__init__()
        self.header = None
        self.payload = None
        self.jobid=jobid
        self.api = 'job/%s' % self.jobid
        self.args = [
                        HTTPParam("action", True),
                        HTTPParam("type", False),
                        HTTPParam("scope", False),
                        HTTPParam("refresh", False),
                        HTTPParam("nocleanup", False),
                        HTTPParam("value", False)
                    ]

        self.method = HTTPMethod.PUT
        self.action = action
        self.type = type
        self.scope = scope
        self.refresh = refresh
        self.nocleanup = nocleanup
        self.value=value