package actions.controller.ajax;

import actions.controller.UserAction;


/**
 * AJAX action. This action is mapped in struts.xml to produce JSON. The JSON parser will iterate through the action
 * and append all getters to the JSON output. In particular, the .JSP is expecting a success boolean value which
 * indicates the success or failure of the operation. Note that if we removed the getter (isSuccess()),
 * our .jsp would have problems.
 * NOTE: ALL SUBCLASSES MUST IMPLEMENT #isSuccess() like this:
 * <code>
 *     public boolean isSuccess() {
 *         return super.isSuccess();
 *     }
 * </code>
 */
public abstract class AjaxAction extends UserAction {

    private boolean success;

    @Override
    public String doWork() {
        doAjaxWork();
        return SUCCESS;
    }

    public abstract void doAjaxWork();

    public boolean isSuccess() {
        return success;
    }

    protected void ajaxSuccess() {
        this.success = true;
    }

    protected void ajaxFailure() {
        this.success = false;
    }

    protected void setAjaxStatus(boolean s) {
        this.success = s;
    }
}
