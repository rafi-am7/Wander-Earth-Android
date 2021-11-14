# Wander Earth – Android Mobile Application
‘Wander Earth’ is a platform where consumers can get all hotels in one platform. They can also compare rooms by ratings and prices of hotels. Hotels also can provide their services through this applictaion. 

## Objectives
* To assemble all hotels in one platform
* To provide the ratings of hotel room
* To make travelling easier

## Features

**1. User Registration:** If the user wants to use the Wanter Earth, they must download the application from the play store, install and register it by providing login information. Once, they registers the registered information is stored on the server and can be validated, checking the valid credentials for the next time he logins with the application. Note that there will be two types of member type during sign up: Hotel and Consumer Account.

**2.Select Hotel for Consumer:** In consumer account there is a spinner for selecting hotels. The hotels which has opened their booking will be shown on this module.


**3. See rooms for Consumer:** After selecting hotel user will click on see room button, they will select the date from calender. The rooms will be shown, if booking is open on the selected date. Note that the booked room color is green, otherwise it is yellow.

**4. Checkout and Payment:** If a user click on unbooked rooms (yellow color) , a dialogue box will open for the confirmation of payment . If the user proceed it will take them another activity for payment. If the user pay the amount the room will be booked, balance will added to the hotel account. Rooms that has booked (green color) can’t be booked further .

**5. Give Rating of Rooms for Consumer:** If a consumer click on booked room ( green color) there can be two things: booked by him/her and booked by others. If he booked the rooms a dialogue box will open to take the rating. A user can give rating only one time for one booking. If the room is booked by others, nothing will heapen (a toast will show that the room is booked).

**6. Enter Room Properties and Open booking for hotel:** In hotel account a hotel can enter their room properties ( room no, floor no, averege room prize). In this version, the hotel can enter these properties only one time (room properties can edit in the future update). The user can open booking by selecting to the date till which booking will be opened (from today). 

**7. Mark room as booked for Hotel:** If a room booked in offline the hotel can marked the room as booked. If the booking canceled, the hotel can mark the room as unbooked.

**8. Change & Forget Password:** Users can update their password by entering the old password. If a  user forget the password, he can update the password by email authantication.

## Application Design
![we hotel flowchart](https://user-images.githubusercontent.com/70595051/141676434-ff9f6f2b-53f2-4b7c-8f67-de642407b162.png)

## Database Design
![we hotel schema diagram](https://user-images.githubusercontent.com/70595051/141676489-12c56447-6f37-4afc-93ab-d72a971e5cd1.png)

## Project Screenshots
| ![ss1](https://user-images.githubusercontent.com/70595051/141676605-6c29d713-8e65-4d7a-b291-6e137618cb32.jpg) | ![ss2](https://user-images.githubusercontent.com/70595051/141676626-6f4db6cb-ecb7-4a2d-8854-b166f6b79135.jpg) | ![ss3](https://user-images.githubusercontent.com/70595051/141676627-36f4aa83-c23a-40f2-8803-41187adcba49.jpg) | 
![ss4](https://user-images.githubusercontent.com/70595051/141676628-c6c6af05-a48c-49d8-878e-cd9e417b381e.jpg)
![ss5](https://user-images.githubusercontent.com/70595051/141676631-61cd3336-986b-4f0e-aee2-de723db8dc95.jpg)
![ss6](https://user-images.githubusercontent.com/70595051/141676635-65bdcbc9-8263-43aa-9556-456bc24a864b.jpg)
![ss7](https://user-images.githubusercontent.com/70595051/141676637-ca5e593b-e3fe-4afa-be56-a8e054591197.jpg)
![ss8](https://user-images.githubusercontent.com/70595051/141676640-7c81c362-ac32-4089-8792-0dcdbb55149a.jpg)
![ss9](https://user-images.githubusercontent.com/70595051/141676644-618a865b-6200-48c4-b889-751dbcdd072c.jpg)
![ss10](https://user-images.githubusercontent.com/70595051/141676646-f65614c7-fbb1-4340-9d55-1414f214eab1.jpeg)
![ss11](https://user-images.githubusercontent.com/70595051/141676649-430b6e52-1840-43da-b8a2-bde04e4137c4.jpg)


