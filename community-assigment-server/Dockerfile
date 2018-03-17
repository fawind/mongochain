FROM python:alpine

RUN pip install flask

COPY . .

EXPOSE 5000

RUN chmod +x start-communityAssignmentService.sh
CMD /start-communityAssignmentService.sh
