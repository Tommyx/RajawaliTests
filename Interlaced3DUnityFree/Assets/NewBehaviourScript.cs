using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class NewBehaviourScript : MonoBehaviour {

	// Use this for initialization
	
    private Vector3 firstpoint; //change type on Vector3
    private Vector3 secondpoint;
    private float xAngle= 0.0f;
    private float yAngle = 0.0f;
    private float xAngTemp= 0.0f;
    private float yAngTemp= 0.0f;
    public float perspectiveZoomSpeed = 0.5f;        // The rate of change of the field of view in perspective mode.

    void Start() {
        xAngle = 0.0f;
        yAngle = 0.0f;
        this.transform.rotation = Quaternion.Euler(yAngle, xAngle, 0.0f);
    }

    void Update () {
	    if(Input.touchCount > 0) {
        
        if(Input.GetTouch(0).phase == TouchPhase.Began) {
           firstpoint = Input.GetTouch(0).position;
           xAngTemp = xAngle;
           yAngTemp = yAngle;
        }
        if(Input.GetTouch(0).phase==TouchPhase.Moved) {
            secondpoint = Input.GetTouch(0).position;
            xAngle = xAngTemp + (secondpoint.x - firstpoint.x) * 180.0f / Screen.width;
            yAngle = yAngTemp - (secondpoint.y - firstpoint.y) *90.0f / Screen.height;
        
            this.transform.rotation = Quaternion.Euler(yAngle, xAngle, 0.0f);
        }

          if (Input.touchCount == 2)
        {
            // Store both touches.
            Touch touchZero = Input.GetTouch(0);
            Touch touchOne = Input.GetTouch(1);

            // Find the position in the previous frame of each touch.
            Vector2 touchZeroPrevPos = touchZero.position - touchZero.deltaPosition;
            Vector2 touchOnePrevPos = touchOne.position - touchOne.deltaPosition;

            // Find the magnitude of the vector (the distance) between the touches in each frame.
            float prevTouchDeltaMag = (touchZeroPrevPos - touchOnePrevPos).magnitude;
            float touchDeltaMag = (touchZero.position - touchOne.position).magnitude;

            // Find the difference in the distances between each frame.
            float deltaMagnitudeDiff = prevTouchDeltaMag - touchDeltaMag;

            // If the camera is orthographic...
            
            // Otherwise change the field of view based on the change in distance between the touches.
            //this.fieldOfView += deltaMagnitudeDiff * perspectiveZoomSpeed;

            // Clamp the field of view to make sure it's between 0 and 180.
            //this.fieldOfView = Mathf.Clamp(this.fieldOfView, 0.1f, 179.9f);
            
        }
    
   }
    } }