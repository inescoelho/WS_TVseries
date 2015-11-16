package actions.controller;

import actions.model.Client;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import utils.MyLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Class which all other Actions are meant to extend. This class encapsulates the session handling logic. It does
 * this by storing the current client (represented by a Client object) in the current session,
 * and either creating it or reading it as necessary. Child classes are expected to call super.execute() to maintain
 * class state as the first step of their own execute() methods. This means that all child classes will eventually
 * invoke getClientSession().
 *
 * A session variable is exposed to children classes, as well as the client object,
 * which is guaranteed by UserAction to always be valid *IF* they comply and call ClientAction's execute method.
 */
public abstract class UserAction extends ActionSupport implements SessionAware, ServletRequestAware,
                                                                  ServletResponseAware {

    protected Map<String, Object> session;
    protected Client client;
    protected HttpServletResponse servletResponse;
    protected HttpServletRequest servletRequest;

    /**
     * Needed by SessionAware
     * @param session Used by struts to set the session
     */
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    /**
     * Synchronizes this.client and the current session, either creating it if session doesn't have it,
     * or loading it from the session if it exists.
     */
    private void getClientSession() {
        if ( !session.containsKey("client") ) {
            this.client = new Client();
            session.put("client", client);
        } else {
            this.client = (Client) session.get("client");
        }
    }

    /**
     * Action's execute method, called whenever the action is triggered.
     * @return A String object, informing the success or failure of the operation.
     * @throws Exception    Throws an exception, in case of an error occurrs when accessing to the database.
     */
    public final String execute() throws Exception {
        getClientSession();

        return doWork();
    }

    public abstract String doWork();

    @Override
    public void setServletResponse(javax.servlet.http.HttpServletResponse httpServletResponse) {
        this.servletResponse = httpServletResponse;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.servletRequest = httpServletRequest;
    }

}
